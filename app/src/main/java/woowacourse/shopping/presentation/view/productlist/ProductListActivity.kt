package woowacourse.shopping.presentation.view.productlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.RetrofitBuilder
import woowacourse.shopping.data.respository.cart.CartRepositoryImpl
import woowacourse.shopping.data.respository.cart.source.local.CartLocalDataSourceImpl
import woowacourse.shopping.data.respository.cart.source.remote.CartRemoteDataSourceImpl
import woowacourse.shopping.data.respository.product.ProductRepositoryImpl
import woowacourse.shopping.data.respository.product.source.remote.ProductRemoteDataSourceImpl
import woowacourse.shopping.data.respository.recentproduct.RecentProductRepositoryImpl
import woowacourse.shopping.data.respository.recentproduct.source.local.RecentProductLocalDataSourceImpl
import woowacourse.shopping.databinding.ActivityProductListBinding
import woowacourse.shopping.databinding.LayoutToolbarCartBinding
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.RecentProductModel
import woowacourse.shopping.presentation.view.cart.CartActivity
import woowacourse.shopping.presentation.view.orderlist.OrderListActivity
import woowacourse.shopping.presentation.view.productdetail.ProductDetailActivity
import woowacourse.shopping.presentation.view.productlist.adpater.MoreProductListAdapter
import woowacourse.shopping.presentation.view.productlist.adpater.ProductListAdapter
import woowacourse.shopping.presentation.view.productlist.adpater.RecentProductListAdapter
import woowacourse.shopping.presentation.view.productlist.adpater.RecentProductWrapperAdapter
import woowacourse.shopping.presentation.view.productlist.adpater.ViewType
import woowacourse.shopping.presentation.view.util.getSerializableCompat
import woowacourse.shopping.presentation.view.util.showToast

class ProductListActivity : AppCompatActivity(), ProductContract.View {
    private lateinit var binding: ActivityProductListBinding
    private lateinit var toolbarCartBinding: LayoutToolbarCartBinding

    private lateinit var url: Server.Url
    private lateinit var token: Server.Token

    private lateinit var retrofitBuilder: RetrofitBuilder

    private lateinit var presenter: ProductContract.Presenter

    private val recentProductResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            presenter.loadRecentProductItems()
            presenter.loadCartItems()
        }

    private val cartResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                presenter.loadCartItems()
            }
        }

    private val productListener = object : ProductListener {
        override fun onCountClick(productId: Long, count: Int) {
            presenter.updateCount(productId, count)
        }

        override fun onItemClick(productId: Long) {
            onProductClickEvent(productId)
        }
    }

    private val productListAdapter by lazy { ProductListAdapter(productListener) }
    private val recentProductListAdapter by lazy { RecentProductListAdapter(::onProductClickEvent) }
    private val recentProductWrapperAdapter by lazy {
        RecentProductWrapperAdapter(
            presenter::getRecentProductsLastScroll,
            presenter::updateRecentProductsLastScroll,
            recentProductListAdapter,
        )
    }
    private val moreProductListAdapter by lazy {
        MoreProductListAdapter {
            presenter.updateProductItems(productListAdapter.itemCount)
        }
    }

    private val concatAdapter: ConcatAdapter by lazy {
        val config = ConcatAdapter.Config.Builder().apply {
            setIsolateViewTypes(false)
        }.build()

        ConcatAdapter(config).apply {
            setConcatAdapter()
        }
    }

    private fun ConcatAdapter.setConcatAdapter() {
        addAdapter(recentProductWrapperAdapter)
        addAdapter(productListAdapter)
        addAdapter(moreProductListAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_list)
        toolbarCartBinding = LayoutToolbarCartBinding.inflate(layoutInflater)

        url = intent.getSerializableCompat(KEY_SERVER_SERVER) ?: return finish()
        token = intent.getSerializableCompat(KEY_SERVER_TOKEN) ?: return finish()
        retrofitBuilder = RetrofitBuilder.getInstance(url, token)

        binding.rvProductList.itemAnimator = null

        setPresenter()
        initLayoutManager()
        presenter.initRecentProductItems()
        presenter.initProductItems()
        setConcatAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_product_list_toolbar, menu)

        menu.findItem(R.id.action_cart)?.run {
            actionView = toolbarCartBinding.root
            setToolbarCart()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_order_list -> {
                startActivity(OrderListActivity.createIntent(this, url, token))
            }
        }
        return true
    }

    private fun setPresenter() {
        val productRemoteDataSource = ProductRemoteDataSourceImpl(retrofitBuilder.createProductService())
        val cartRemoteDataSource = CartRemoteDataSourceImpl(retrofitBuilder.createCartService())
        val cartLocalDataSource = CartLocalDataSourceImpl(this, url)
        val recentProductLocalDataSource = RecentProductLocalDataSourceImpl(this, url)
        presenter = ProductListPresenter(
            this,
            productRepository = ProductRepositoryImpl(productRemoteDataSource),
            cartRepository = CartRepositoryImpl(cartLocalDataSource, cartRemoteDataSource),
            recentProductRepository = RecentProductRepositoryImpl(
                productRemoteDataSource,
                recentProductLocalDataSource
            ),
        )
    }

    private fun setToolbarCart() {
        toolbarCartBinding.viewToolbarCart.setOnClickListener {
            presenter.actionOptionItem()
        }
    }

    override fun setVisibleToolbarCartCountView() {
        toolbarCartBinding.tvToolbarCartCount.post {
            toolbarCartBinding.tvToolbarCartCount.visibility = View.VISIBLE
        }
    }

    override fun hideGoneToolbarCartCountView() {
        toolbarCartBinding.tvToolbarCartCount.post {
            toolbarCartBinding.tvToolbarCartCount.visibility = View.GONE
        }
    }

    override fun setLayoutVisibility() {
        binding.rvProductList.post {
            binding.rvProductList.visibility = View.VISIBLE
        }
        binding.layoutSkeletonProductList.post {
            binding.layoutSkeletonProductList.visibility = View.GONE
        }
    }

    override fun updateToolbarCartCountView(count: Int) {
        toolbarCartBinding.tvToolbarCartCount.post {
            toolbarCartBinding.tvToolbarCartCount.text = count.toString()
        }
    }

    private fun initLayoutManager() {
        val layoutManager = GridLayoutManager(this, SPAN_SIZE)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (concatAdapter.getItemViewType(position)) {
                    ViewType.PRODUCT_LIST.ordinal -> SPAN_SIZE_OF_TWO_COLUMN
                    else -> SPAN_SIZE_OF_ONE_COLUMN
                }
            }
        }
        binding.rvProductList.layoutManager = layoutManager
    }

    override fun showProductItemsView(cartProducts: List<CartProductModel>) {
        binding.rvProductList.post {
            productListAdapter.setItems(cartProducts)
        }
    }

    override fun showRecentProductItemsView(recentProducts: List<RecentProductModel>) {
        binding.rvProductList.post {
            recentProductListAdapter.setItems(recentProducts)
        }
    }

    private fun setConcatAdapter() {
        binding.rvProductList.adapter = concatAdapter
    }

    private fun onProductClickEvent(productId: Long) {
        val recentProduct = presenter.getLastRecentProductItem(0)
        presenter.saveRecentProduct(productId)

        val intent =
            ProductDetailActivity.createIntent(this, productId, recentProduct, url, token)
        recentProductResultLauncher.launch(intent)
    }

    override fun moveToCartView() {
        cartResultLauncher.launch(CartActivity.createIntent(this, url, token))
    }

    override fun handleErrorView(message: String) {
        binding.root.post {
            showToast(message)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_STATE_LAST_SCROLL, presenter.getRecentProductsLastScroll())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        presenter.updateRecentProductsLastScroll(savedInstanceState.getInt(KEY_STATE_LAST_SCROLL))
    }

    companion object {
        private const val SPAN_SIZE = 2
        private const val SPAN_SIZE_OF_ONE_COLUMN = 2
        private const val SPAN_SIZE_OF_TWO_COLUMN = 1

        private const val KEY_STATE_LAST_SCROLL = "KEY_STATE_LAST_SCROLL"
        internal const val KEY_SERVER_SERVER = "KEY_SERVER_SERVER"
        internal const val KEY_SERVER_TOKEN = "KEY_SERVER_TOKEN"

        fun createIntent(context: Context, server: Server.Url, token: Server.Token): Intent {
            val intent = Intent(context, ProductListActivity::class.java)
            intent.putExtra(KEY_SERVER_SERVER, server)
            intent.putExtra(KEY_SERVER_TOKEN, token)
            return intent
        }
    }
}
