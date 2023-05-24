package woowacourse.shopping.ui.products

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import woowacourse.shopping.R
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
import woowacourse.shopping.ui.products.adapter.ProductListAdapter
import woowacourse.shopping.ui.products.adapter.RecentlyViewedProductListAdapter
import woowacourse.shopping.ui.products.uistate.ProductUIState
import woowacourse.shopping.ui.products.uistate.RecentlyViewedProductUIState
import woowacourse.shopping.utils.ServerConfiguration
import woowacourse.shopping.utils.customview.CountBadge

class ProductListActivity : AppCompatActivity(), ProductListContract.View {

    private val binding: ActivityProductListBinding by lazy {
        ActivityProductListBinding.inflate(layoutInflater)
    }

    private val presenter: ProductListContract.Presenter by lazy { createPresenter() }

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

    private val productListAdapter: ProductListAdapter by lazy {
        ProductListAdapter(
            mutableListOf(),
            { ProductDetailActivity.startActivity(this, it) },
            { presenter.onAddToCart(it) },
            { presenter.onPlusCount(it) },
            { presenter.onMinusCount(it) }
        )
    }

    private lateinit var cartCountBadge: CountBadge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActionBar()

        initProductList()
        initLoadingButton()
        if (savedInstanceState != null) {
            presenter.restoreCurrentPage(savedInstanceState.getInt(CURRENT_PAGE))
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.onLoadRecentlyViewedProducts()
        presenter.onRefreshProducts()
        presenter.onLoadCartItemCount()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_product_list, menu)

        cartCountBadge =
            menu.findItem(R.id.cart_count_badge).actionView?.findViewById(R.id.cart_count_badge)
                ?: throw IllegalStateException("장바구니 아이템 개수 배지가 메뉴에 없으면 메뉴 리소스를 다시 보세요.")
        cartCountBadge.isVisible = false

        presenter.onLoadCartItemCount()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                CartActivity.startActivity(this)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbarProductList)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initProductList() {
        binding.recyclerViewMainProduct.adapter = productListAdapter
    }

    private fun initLoadingButton() {
        binding.btnLoading.setOnClickListener {
            presenter.onLoadProductsNextPage()
        }
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

    private fun setUIAboutRecentlyViewedProductIsVisible(isVisible: Boolean) {
        binding.tvRecentlyViewedProduct.isVisible = isVisible
        binding.recyclerViewRecentlyViewed.isVisible = isVisible
        binding.viewSeparatorRecyclerView.isVisible = isVisible
    }

    override fun addProducts(products: List<ProductUIState>) {
        runOnUiThread {
            productListAdapter.addItems(products)
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

    override fun replaceProduct(product: ProductUIState) {
        runOnUiThread {
            productListAdapter.replaceItem(product)
        }
    }

    override fun setCartItemCount(count: Int) {
        runOnUiThread {
            if (count == 0) {
                cartCountBadge.isVisible = false
                return@runOnUiThread
            }
            cartCountBadge.isVisible = true
            cartCountBadge.updateCount(count)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(CURRENT_PAGE, presenter.getCurrentPage())
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val CURRENT_PAGE = "CURRENT_PAGE"
    }
}
