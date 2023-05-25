package woowacourse.shopping.feature.product

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.example.domain.CartProduct
import com.example.domain.Product
import com.example.domain.RecentProduct
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.R
import woowacourse.shopping.ServerType
import woowacourse.shopping.common.adapter.LoadMoreAdapter
import woowacourse.shopping.data.cart.CartRemoteService
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.product.ProductRemoteService
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recentproduct.RecentProductDao
import woowacourse.shopping.data.recentproduct.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityMainBinding
import woowacourse.shopping.feature.cart.CartActivity
import woowacourse.shopping.feature.product.detail.ProductDetailActivity
import woowacourse.shopping.feature.product.recent.RecentProductListAdapter
import woowacourse.shopping.feature.product.recent.RecentProductListWrapperAdapter
import woowacourse.shopping.model.ProductState
import woowacourse.shopping.model.RecentProductState
import woowacourse.shopping.model.mapper.toUi
import woowacourse.shopping.util.SpanSizeLookUpManager
import woowacourse.shopping.util.extension.showToast

class MainActivity : AppCompatActivity(), MainContract.View {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    private val serverUrl by lazy { intent.getStringExtra(ServerType.INTENT_KEY) ?: "" }
    private val cartRepository: CartRepository by lazy {
        CartRepositoryImpl(serverUrl, CartRemoteService())
    }
    private val presenter: MainContract.Presenter by lazy {
        val productRemoteService = ProductRemoteService()
        val productRepository: ProductRepository =
            ProductRepositoryImpl(serverUrl, productRemoteService)
        val recentProductRepository: RecentProductRepository =
            RecentProductRepositoryImpl(serverUrl, productRemoteService, RecentProductDao(this))
        MainPresenter(this, productRepository, recentProductRepository, cartRepository)
    }
    private val productListAdapter: ProductListAdapter by lazy {
        ProductListAdapter(
            cartProductStates = listOf(),
            onProductClick = presenter::showProductDetail,
            cartProductAddFab = { Thread { presenter.storeCartProduct(it) }.start() },
            cartProductCountMinus = { Thread { presenter.minusCartProductCount(it) }.start() },
            cartProductCountPlus = { Thread { presenter.plusCartProductCount(it) }.start() }
        )
    }
    private val recentProductListAdapter by lazy {
        RecentProductListAdapter(emptyList())
    }
    private val recentProductListWrapperAdapter: RecentProductListWrapperAdapter by lazy {
        RecentProductListWrapperAdapter(recentProductListAdapter)
    }
    private val loadMoreAdapter: LoadMoreAdapter by lazy {
        LoadMoreAdapter(onClick = { presenter.loadMoreProducts() })
    }

    private val concatAdapter: ConcatAdapter by lazy {
        val config = ConcatAdapter.Config.Builder().apply {
            setIsolateViewTypes(false)
        }.build()
        ConcatAdapter(config, recentProductListWrapperAdapter, productListAdapter, loadMoreAdapter)
    }

    private var cartCountBadge: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initList()
        runOnUiThread { presenter.loadMoreProducts() }

        cartRepository.getAll(
            onFailure = {}, onSuccess = { presenter.loadCartProductCounts() }
        )
    }

    override fun onResume() {
        super.onResume()
        runOnUiThread {
            presenter.loadRecentProducts()
            presenter.loadCartProductCountBadge()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)
        cartCountBadge =
            menu?.findItem(R.id.cart_count_badge)?.actionView?.findViewById(R.id.badge)
        presenter.loadCartProductCountBadge()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cart -> CartActivity.startActivity(this, serverUrl)
        }
        return true
    }

    override fun addProductItems(products: List<ProductState>) {
        runOnUiThread { productListAdapter.addItems(products) }
    }

    override fun setProducts(products: List<Product>) {
        runOnUiThread { productListAdapter.setItems(products.map(Product::toUi)) }
    }

    override fun setRecentProducts(recentProducts: List<RecentProduct>) {
        runOnUiThread { recentProductListAdapter.setItems(recentProducts.map(RecentProduct::toUi)) }
    }

    override fun setCartProductCountBadge(count: Int) {
        runOnUiThread { cartCountBadge?.text = count.toString() }
    }

    override fun setCartProductCounts(cartProducts: List<CartProduct>) {
        runOnUiThread { productListAdapter.setCartProducts(cartProducts.map(CartProduct::toUi)) }
    }

    override fun showProductDetail(
        productState: ProductState,
        recentProductState: RecentProductState?
    ) {
        ProductDetailActivity.startActivity(this, serverUrl, productState, recentProductState)
    }

    override fun showEmptyProducts() = runOnUiThread { showToast("제품이 없습니다.") }

    override fun showCartProductCountBadge() {
        runOnUiThread { cartCountBadge?.visibility = VISIBLE }
    }

    override fun hideCartProductCount() {
        runOnUiThread { cartCountBadge?.visibility = GONE }
    }

    private fun initList() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        gridLayoutManager.spanSizeLookup =
            SpanSizeLookUpManager(concatAdapter, gridLayoutManager.spanCount)

        binding.productRv.layoutManager = gridLayoutManager
        binding.productRv.adapter = concatAdapter
    }

    companion object {
        fun getIntent(context: Context, serverUrl: String): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(ServerType.INTENT_KEY, serverUrl)
            return intent
        }
    }
}
