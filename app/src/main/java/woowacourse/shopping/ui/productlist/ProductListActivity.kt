package woowacourse.shopping.ui.productlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import woowacourse.shopping.data.cart.CartItemRemoteService
import woowacourse.shopping.data.cart.CartItemRepositoryImpl
import woowacourse.shopping.data.database.DbHelper
import woowacourse.shopping.data.product.ProductRemoteService
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recentlyviewedproduct.RecentlyViewedProductMemoryDao
import woowacourse.shopping.data.recentlyviewedproduct.RecentlyViewedProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityProductListBinding
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.productdetail.ProductDetailActivity
import woowacourse.shopping.ui.productlist.adapter.ProductListAdapter
import woowacourse.shopping.ui.productlist.adapter.RecentlyViewedProductListAdapter
import woowacourse.shopping.ui.productlist.uistate.ProductUIState
import woowacourse.shopping.ui.productlist.uistate.RecentlyViewedProductUIState
import woowacourse.shopping.utils.ServerConfiguration

class ProductListActivity : AppCompatActivity(), ProductListContract.View {
    private val binding: ActivityProductListBinding by lazy {
        ActivityProductListBinding.inflate(layoutInflater)
    }

    private val presenter: ProductListContract.Presenter by lazy { createPresenter() }

    private val productListAdapter: ProductListAdapter by lazy {
        ProductListAdapter(
            productListEvent = makeCounterEvent()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()
        initProductList()
        initLoadingButton()
    }

    override fun onStart() {
        super.onStart()
        presenter.loadRecentlyViewedProducts()
        presenter.refreshProducts()
        presenter.loadCartItemCount()
    }

    override fun setRecentlyViewedProducts(recentlyViewedProducts: List<RecentlyViewedProductUIState>) {
        runOnUiThread {
            if (recentlyViewedProducts.isEmpty()) {
                setUIAboutRecentlyViewedProductIsVisible(false)
                return@runOnUiThread
            }
            setUIAboutRecentlyViewedProductIsVisible(true)

            binding.recyclerViewRecentlyViewed.adapter =
                RecentlyViewedProductListAdapter(recentlyViewedProducts) {
                    ProductDetailActivity.startActivity(this, it)
                }
        }
    }

    override fun addProducts(products: List<ProductUIState>) {
        runOnUiThread {
            productListAdapter.addItems(products)
        }
    }

    override fun changeProduct(product: ProductUIState) {
        runOnUiThread {
            productListAdapter.changeItem(product)
        }
    }

    override fun setProducts(products: List<ProductUIState>) {
        runOnUiThread {
            binding.layoutSkeletonProductList.isVisible = false
            binding.viewProductList.isVisible = true
            productListAdapter.setItems(products)
            binding.recyclerViewMainProduct.smoothScrollToPosition(0)
        }
    }

    override fun setCanLoadMore(canLoadMore: Boolean) {
        runOnUiThread {
            binding.btnLoading.isVisible = canLoadMore
        }
    }

    override fun setCartItemCount(count: Int) {
        runOnUiThread {
            binding.cartCount = count
        }
    }

    override fun showCart() {
        CartActivity.startActivity(this)
    }

    private fun initToolBar() {
        setSupportActionBar(binding.toolbarProductList)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.ibCartButton.setOnClickListener {
            presenter.openCart()
        }
        binding.cartCount = 0
    }

    private fun initProductList() {
        binding.recyclerViewMainProduct.adapter = productListAdapter
    }

    private fun initLoadingButton() {
        binding.btnLoading.setOnClickListener {
            presenter.loadProductsNextPage()
        }
    }

    private fun createPresenter(): ProductListPresenter {
        val productRemoteService = ProductRemoteService(ServerConfiguration.host)
        val dbHelper = DbHelper.getDbInstance(this)
        val recentlyViewedProductMemoryDao = RecentlyViewedProductMemoryDao(dbHelper)
        val recentlyViewedProductRepositoryImpl = RecentlyViewedProductRepositoryImpl(
            recentlyViewedProductMemoryDao, productRemoteService
        )
        val cartItemRepositoryImpl = CartItemRepositoryImpl(
            CartItemRemoteService(ServerConfiguration.host)
        )
        val productRepositoryImpl = ProductRepositoryImpl(productRemoteService)
        return ProductListPresenter(
            this, recentlyViewedProductRepositoryImpl, productRepositoryImpl, cartItemRepositoryImpl
        )
    }

    private fun setUIAboutRecentlyViewedProductIsVisible(isVisible: Boolean) {
        binding.tvRecentlyViewedProduct.isVisible = isVisible
        binding.recyclerViewRecentlyViewed.isVisible = isVisible
        binding.viewSeparatorRecyclerView.isVisible = isVisible
    }

    private fun makeCounterEvent() = object : ProductListEvent {
        override fun onClick(productId: Long) {
            ProductDetailActivity.startActivity(this@ProductListActivity, productId)
        }

        override fun onClickAddToCartButton(productId: Long) {
            presenter.addProductToCart(productId)
        }

        override fun onClickPlus(id: Long) {
            presenter.plusCount(id)
        }

        override fun onClickMinus(id: Long) {
            presenter.minusCount(id)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, this::class.java).apply {}
            context.startActivity(intent)
        }
    }
}
