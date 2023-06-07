package woowacourse.shopping.feature.cart

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.SimpleItemAnimator
import woowacourse.shopping.R
import woowacourse.shopping.data.preferences.UserPreference
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.feature.order.confirm.OrderConfirmActivity
import woowacourse.shopping.module.ApiModule
import woowacourse.shopping.util.getSerializableExtraCompat
import woowacourse.shopping.util.showToastNetworkError
import woowacourse.shopping.util.showToastShort
import woowacourse.shopping.util.toMoneyFormat

class CartActivity : AppCompatActivity(), CartContract.View {
    private lateinit var binding: ActivityCartBinding
    private lateinit var presenter: CartContract.Presenter
    private lateinit var cartProductAdapter: CartProductAdapter

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private val cartProductClickListener: CartProductClickListener by lazy {
        object : CartProductClickListener {
            override fun onDeleteClick(cartId: Long) {
                presenter.handleDeleteCartProductClick(cartId)
            }

            override fun onCartCountChanged(cartId: Long, count: Int) {
                presenter.handleCartProductCartCountChange(cartId, count)
            }

            override fun onSelectedPurchaseChanged(cartId: Long, checked: Boolean) {
                presenter.handlePurchaseSelectedCheckedChange(cartId, checked)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        cartProductAdapter = CartProductAdapter(cartProductClickListener)
        binding.cartItemRecyclerview.adapter = cartProductAdapter
        initPresenter()
        presenter.loadInitCartProduct()
        binding.presenter = presenter
        supportActionBar?.title = getString(R.string.cart)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setRecyclerViewAnimator()
        observePresenter()

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val cartIds =
                        result.data?.getSerializableExtraCompat<ArrayList<Long>>(
                            OrderConfirmActivity.ORDER_CART_ID_KEY
                        )
                    cartIds?.let { presenter.processRemoveOrderCheckedItems() }
                }
            }
    }

    private fun initPresenter() {
        val cartProductRemoteService = ApiModule.getInstance(UserPreference).createCartService()
        presenter = CartPresenter(this, CartRepositoryImpl(cartProductRemoteService))
    }

    private fun setRecyclerViewAnimator() {
        val animator = binding.cartItemRecyclerview.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    private fun observePresenter() {
        presenter.currentPageCartProducts.observe(this) {
            cartProductAdapter.setItems(it)
        }
        presenter.pageBottomNavigationUiModel.observe(this) {
            binding.previousPageBtn.isEnabled = it.hasPreviousPage
            binding.nextPageBtn.isEnabled = it.hasNextPage
            binding.pageCountTextView.text = it.currentPageNumber.toString()
        }
        presenter.cartBottomNavigationUiModel.observe(this) {
            setAllCheckedButtonState(it.isCurrentPageAllChecked)

            binding.orderCount = it.checkedCount
            binding.orderConfirmView.isEnabled = it.isAnyChecked

            binding.money = it.totalCheckedMoney.toMoneyFormat()
        }
    }

    private fun setAllCheckedButtonState(isAllChecked: Boolean) {
        binding.allCheckView.setOnCheckedChangeListener { _, _ -> }
        binding.allCheckView.isChecked = isAllChecked
        binding.allCheckView.setOnCheckedChangeListener { _, isChecked ->
            presenter.handleCurrentPageAllCheckedChange(isChecked)
        }
    }

    override fun showFailedLoadCartInfo() {
        showToastShort(R.string.failed_load_cart_info)
    }

    override fun reBindProductItem(cartId: Long) {
        cartProductAdapter.reBindItem(cartId)
    }

    override fun exitCartScreen() = finish()

    override fun hideLoadingView() {
        runOnUiThread {
            binding.cartLayout.visibility = View.VISIBLE
            binding.skeletonCartLoadingLayout.visibility = View.GONE
        }
    }

    override fun showLoadingView() {
        binding.cartLayout.visibility = View.GONE
        binding.skeletonCartLoadingLayout.visibility = View.VISIBLE
    }

    override fun showOrderConfirmScreen(cartIds: List<Long>) {
        resultLauncher.launch(OrderConfirmActivity.getIntent(this, cartIds))
    }

    override fun showOrderUnavailableMessage() {
        showToastShort(R.string.order_unavailable_message)
    }

    override fun showFailedChangeCartCount() {
        showToastShort(R.string.failed_change_cart_count)
    }

    override fun showFailedOrderRequest() {
        showToastShort(R.string.failed_order_request)
    }

    override fun showNetworkError() {
        showToastNetworkError()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                presenter.exit()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }
    }
}
