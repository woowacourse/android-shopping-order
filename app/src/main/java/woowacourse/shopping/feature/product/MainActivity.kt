package woowacourse.shopping.feature.product

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.example.domain.cart.CartProduct
import com.example.domain.cart.CartRepository
import com.example.domain.product.Product
import com.example.domain.product.ProductRepository
import com.example.domain.product.recent.RecentProduct
import com.example.domain.product.recent.RecentProductRepository
import woowacourse.shopping.ServerType
import woowacourse.shopping.common.adapter.LoadMoreAdapter
import woowacourse.shopping.data.cart.CartRemoteRepository
import woowacourse.shopping.data.product.ProductRemoteRepository
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
    private val presenter: MainContract.Presenter by lazy {
        val cartRepository: CartRepository = CartRemoteRepository(url = serverUrl)
        val productRepository: ProductRepository = ProductRemoteRepository(serverUrl)
        val recentProductRepository: RecentProductRepository =
            RecentProductRepositoryImpl(this, serverUrl)
        MainPresenter(
            view = this, productRepository = productRepository,
            recentProductRepository = recentProductRepository, cartRepository = cartRepository
        )
    }
    private val productListAdapter: ProductListAdapter by lazy {
        ProductListAdapter(
            cartProductStates = listOf(),
            onProductClick = presenter::showProductDetail,
            cartProductAddFab = { presenter.storeCartProduct(it) },
            cartProductCountMinus = { presenter.minusCartProductQuantity(it) },
            cartProductCountPlus = { presenter.plusCartProductQuantity(it) }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initList()

        presenter.loadMoreProducts()
        initToolBar()
    }

    override fun onResume() {
        super.onResume()
        presenter.loadCartProductsQuantity()
        presenter.loadRecentProducts()
        presenter.loadCartSizeBadge()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun addProductItems(products: List<ProductState>) {
        productListAdapter.addItems(products)
    }

    override fun setProducts(products: List<Product>) {
        productListAdapter.setItems(products.map(Product::toUi))
    }

    override fun setRecentProducts(recentProducts: List<RecentProduct>) {
        recentProductListAdapter.setItems(recentProducts.map(RecentProduct::toUi))
    }

    override fun setCartProductCounts(cartProducts: List<CartProduct>) {
        productListAdapter.setCartProducts(cartProducts.map(CartProduct::toUi))
    }

    override fun showProductDetail(
        productState: ProductState,
        recentProductState: RecentProductState?
    ) {
        ProductDetailActivity.startActivity(this, serverUrl, productState, recentProductState)
    }

    override fun showEmptyProducts() = showToast("제품이 없습니다.")

    override fun setCartProductCountBadge(count: Int) {
        binding.cartCountTv.text = count.toString()
    }

    override fun showCartProductCountBadge() {
        binding.cartCountTv.visibility = VISIBLE
    }

    override fun hideCartProductCount() {
        binding.cartCountTv.visibility = GONE
    }

    override fun showProducts() {
        binding.productRv.visibility = VISIBLE
        binding.skeletonGl.visibility = GONE
    }

    private fun initList() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        gridLayoutManager.spanSizeLookup =
            SpanSizeLookUpManager(concatAdapter, gridLayoutManager.spanCount)

        binding.productRv.layoutManager = gridLayoutManager
        binding.productRv.adapter = concatAdapter
    }

    private fun initToolBar() {
        setSupportActionBar(binding.mainTb)
        binding.cartIv.setOnClickListener {
            CartActivity.startActivity(this, serverUrl)
        }
    }

    companion object {
        fun getIntent(context: Context, serverUrl: String): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(ServerType.INTENT_KEY, serverUrl)
            return intent
        }
    }
}
