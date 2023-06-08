package woowacourse.shopping.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.local.CartDefaultLocalDataSource
import woowacourse.shopping.data.remote.CartRetrofitDataSource
import woowacourse.shopping.data.repository.CartDefaultRepository
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.model.CartProductUIModel
import woowacourse.shopping.model.PageUIModel
import woowacourse.shopping.ui.cart.cartAdapter.CartAdapter
import woowacourse.shopping.ui.cart.cartAdapter.CartListener
import woowacourse.shopping.ui.detailedProduct.DetailedProductActivity
import woowacourse.shopping.ui.order.OrderActivity

class CartActivity : AppCompatActivity(), CartContract.View {
    private lateinit var binding: ActivityCartBinding
    private lateinit var presenter: CartContract.Presenter

    private val adapter: CartAdapter = CartAdapter(getCartListener())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initToolbar()
        initPresenter(savedInstanceState)
        initObserve()
        initView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return false
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.getPageIndex().let {
            outState.putInt(KEY_OFFSET, it)
        }
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initPresenter(savedInstanceState: Bundle?) {
        presenter = CartPresenter(
            this,
            cartRepository = CartDefaultRepository(
                localDataSource = CartDefaultLocalDataSource(),
                remoteDataSource = CartRetrofitDataSource()
            ),
            savedInstanceState?.getInt(KEY_OFFSET) ?: 0
        )
    }

    private fun initObserve() {
        observeCheckedCount()
        observeTotalPrice()
        observeAllCheck()
    }

    private fun observeCheckedCount() {
        presenter.checkedCount.observe(this) {
            binding.cartBottom.totalCount = it
        }
    }

    private fun observeTotalPrice() {
        presenter.totalPrice.observe(this) {
            binding.cartBottom.price = it
        }
    }

    private fun observeAllCheck() {
        presenter.allCheck.observe(this) {
            binding.cartBottom.cbCheckAll.isChecked = it
        }
    }

    private fun initView() {
        binding.rvProducts.adapter = adapter
        binding.rvProducts.itemAnimator = null

        binding.cartBottom.onAllCheckClick = presenter::setUpProductsCheck
        binding.cartBottom.tvOrderProduct.setOnClickListener { presenter.checkOutOrder() }

        presenter.fetchCartProducts()
    }

    private fun getCartListener() = object : CartListener {
        override fun onPageNext() {
            presenter.moveToPageNext()
        }
        override fun onPagePrev() {
            presenter.moveToPagePrev()
        }
        override fun onItemRemove(productId: Int) {
            presenter.updateItemCount(productId, 0)
        }
        override fun onItemClick(product: CartProductUIModel) {
            presenter.navigateToItemDetail(product.productId)
        }
        override fun onItemUpdate(productId: Int, count: Int) {
            presenter.updateItemCount(productId, count)
        }
        override fun onItemCheckChanged(productId: Int, checked: Boolean) {
            presenter.updateItemCheck(productId, checked)
        }
    }

    override fun setPage(page: List<CartProductUIModel>, pageUIModel: PageUIModel) {
        runOnUiThread {
            binding.rvProducts.isVisible = true
            binding.skeletonLayout.isVisible = false
            adapter.submitList(page, pageUIModel)
        }
    }

    override fun navigateToItemDetail(productId: Int) {
        startActivity(DetailedProductActivity.getIntent(this, productId))
    }

    override fun navigateToOrder(cartIds: List<Int>) {
        startActivity(OrderActivity.getIntent(this, cartIds))
    }

    companion object {
        private const val KEY_OFFSET = "KEY_OFFSET"
        fun getIntent(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }
    }
}
