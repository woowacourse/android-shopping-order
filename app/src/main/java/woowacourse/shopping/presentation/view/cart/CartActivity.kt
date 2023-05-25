package woowacourse.shopping.presentation.view.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.SimpleItemAnimator
import woowacourse.shopping.R
import woowacourse.shopping.data.respository.cart.CartRepositoryImpl
import woowacourse.shopping.data.respository.cart.source.remote.CartRemoteDataSourceImpl
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.view.cart.adapter.CartAdapter
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_BASE_URL
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_TOKEN
import woowacourse.shopping.presentation.view.util.showToast

class CartActivity : AppCompatActivity(), CartContract.View {
    private lateinit var binding: ActivityCartBinding

    private lateinit var baseUrl: String
    private lateinit var token: String

    private lateinit var cartAdapter: CartAdapter

    private lateinit var presenter: CartContract.Presenter

    private val cartProductListener = object : CartProductListener {
        override fun onCheckChanged(cartId: Long, checked: Boolean) {
            presenter.updateProductChecked(cartId, checked)
            presenter.calculateTotalPrice()
        }

        override fun onCountClick(cartId: Long, count: Int) {
            presenter.updateProductCount(cartId, count)
            presenter.calculateTotalPrice()
        }

        override fun onDeleteClick(cartId: Long) {
            presenter.deleteCartItem(cartId)
            presenter.calculateTotalPrice()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_OK)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)

        baseUrl = intent.getStringExtra(KEY_SERVER_BASE_URL) ?: return finish()
        token = intent.getStringExtra(KEY_SERVER_TOKEN) ?: return finish()

        setPresenter()
        setSupportActionBar()
        setRecyclerViewAnimator()
        presenter.initCartItems()
        setLeftButtonClick()
        setRightButtonClick()
        setAllProduceCheckedClick()
    }

    private fun setPresenter() {
        val cartRemoteDataSourceImpl = CartRemoteDataSourceImpl(baseUrl, token)
        presenter = CartPresenter(
            this,
            CartRepositoryImpl(this, cartRemoteDataSourceImpl)
        )
    }

    private fun setSupportActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.toolbar_title_cart)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setRecyclerViewAnimator() {
        val animator = binding.rvCart.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    override fun setCartItemsView(carts: List<CartModel>) {
        binding.rvCart.post {
            cartAdapter = CartAdapter(carts, cartProductListener)
            binding.rvCart.adapter = cartAdapter
        }
    }

    override fun setChangedCartItemsView(carts: List<CartModel>) {
        binding.rvCart.post {
            cartAdapter.updateList(carts)
        }
    }

    override fun setEnableLeftButton(isEnabled: Boolean) {
        binding.btCartListPageLeft.post {
            binding.btCartListPageLeft.isEnabled = isEnabled
        }
    }

    override fun setEnableRightButton(isEnabled: Boolean) {
        binding.btCartListPageRight.post {
            binding.btCartListPageRight.isEnabled = isEnabled
        }
    }

    private fun setLeftButtonClick() {
        binding.btCartListPageLeft.setOnClickListener {
            presenter.calculatePreviousPage()
            presenter.loadCartItems()
        }
    }

    private fun setRightButtonClick() {
        binding.btCartListPageRight.setOnClickListener {
            presenter.calculateNextPage()
            presenter.loadCartItems()
        }
    }

    private fun setAllProduceCheckedClick() {
        binding.cbCartAll.setOnCheckedChangeListener { _, isChecked ->
            presenter.updateCurrentPageAllProductChecked(isChecked)
            presenter.calculateTotalPrice()
        }
    }

    override fun updateAllChecking(startPosition: Int, count: Int) {
        cartAdapter.updateAllChecking(startPosition, count)
    }

    override fun setAllCartChecked(isChecked: Boolean) {
        binding.cbCartAll.post {
            binding.cbCartAll.setOnCheckedChangeListener { _, _ -> }
            binding.cbCartAll.isChecked = isChecked
            setAllProduceCheckedClick()
        }
    }

    override fun setPageCountView(page: Int) {
        binding.tvCartListPageCount.text = page.toString()
    }

    override fun setTotalPriceView(totalPrice: Int) {
        binding.totalPrice = totalPrice
    }

    override fun setLayoutVisibility() {
        binding.layoutSkeletonCartList.post {
            binding.layoutSkeletonCartList.visibility = View.GONE
        }
        binding.rvCart.post {
            binding.rvCart.visibility = View.VISIBLE
        }
        binding.linearlayoutCartListPager.post {
            binding.linearlayoutCartListPager.visibility = View.VISIBLE
        }
        binding.clCartBottomContainer.post {
            binding.clCartBottomContainer.visibility = View.VISIBLE
        }
    }

    override fun handleErrorView() {
        binding.root.post {
            showToast(getString(R.string.toast_message_system_error))
        }
    }

    companion object {
        fun createIntent(context: Context, baseUrl: String, token: String): Intent {
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra(KEY_SERVER_BASE_URL, baseUrl)
            intent.putExtra(KEY_SERVER_TOKEN, token)

            return intent
        }
    }
}
