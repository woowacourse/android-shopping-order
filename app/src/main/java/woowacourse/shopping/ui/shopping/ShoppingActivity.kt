package woowacourse.shopping.ui.shopping

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.local.CartDefaultLocalDataSource
import woowacourse.shopping.data.local.product.ProductSqliteDataSource
import woowacourse.shopping.data.local.recentProduct.RecentSqliteProductDataSource
import woowacourse.shopping.data.remote.CartRetrofitDataSource
import woowacourse.shopping.data.remote.ProductRetrofitDataSource
import woowacourse.shopping.data.repository.CartDefaultRepository
import woowacourse.shopping.data.repository.ProductDefaultRepository
import woowacourse.shopping.data.repository.RecentDefaultRepository
import woowacourse.shopping.databinding.ActivityShoppingBinding
import woowacourse.shopping.model.ProductUIModel
import woowacourse.shopping.model.RecentProductUIModel
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.detailedProduct.DetailedProductActivity
import woowacourse.shopping.ui.orderHistories.OrderHistoriesActivity
import woowacourse.shopping.ui.shopping.productAdapter.ProductsAdapter
import woowacourse.shopping.ui.shopping.productAdapter.ProductsAdapterDecoration.getItemDecoration
import woowacourse.shopping.ui.shopping.productAdapter.ProductsAdapterDecoration.getSpanSizeLookup
import woowacourse.shopping.ui.shopping.productAdapter.ProductsListener

class ShoppingActivity : AppCompatActivity(), ShoppingContract.View {
    private lateinit var binding: ActivityShoppingBinding
    private lateinit var presenter: ShoppingContract.Presenter

    private val adapter: ProductsAdapter = ProductsAdapter(getAdapterListener())
    private var tvCount: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initToolbar()
        initPresenter()
        initLayoutManager()
        initView()
    }

    override fun onResume() {
        super.onResume()
        presenter.fetchRecentProducts()
        presenter.fetchCartCounts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cart_menu, menu)
        menu?.findItem(R.id.cart)?.actionView?.let { view ->
            view.setOnClickListener { navigateToCart() }
            view.findViewById<TextView>(R.id.tv_counter)?.let { tvCount = it }
        }
        presenter.fetchTotalCount()
        return true
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shopping)
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun initPresenter() {
        presenter = ShoppingPresenter(
            this,
            productRepository = ProductDefaultRepository(
                localDataSource = ProductSqliteDataSource(this),
                remoteDataSource = ProductRetrofitDataSource()
            ),
            cartRepository = CartDefaultRepository(
                localDataSource = CartDefaultLocalDataSource(),
                remoteDataSource = CartRetrofitDataSource()
            ),
            recentRepository = RecentDefaultRepository(
                localDataSource = RecentSqliteProductDataSource(this)
            )
        )
    }

    private fun initLayoutManager() {
        val layoutManager = binding.rvProducts.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = getSpanSizeLookup(layoutManager, adapter)
        binding.rvProducts.addItemDecoration(getItemDecoration(layoutManager, resources))
    }

    private fun initView() {
        presenter.fetchRecentProducts()
        presenter.fetchNextProducts()
        presenter.fetchCartCounts()

        binding.rvProducts.adapter = adapter
        binding.rvProducts.itemAnimator = null
        binding.bottom.navigateToOrders = presenter::processToOrderHistories
    }

    private fun getAdapterListener() = object : ProductsListener {
        override fun onClickItem(productId: Int) {
            presenter.processToItemDetail(productId)
        }
        override fun onReadMoreClick() {
            presenter.fetchNextProducts()
        }
        override fun onAddCartOrUpdateCount(productId: Int, count: Int) {
            presenter.updateItemCount(productId, count)
            presenter.fetchTotalCount()
        }
    }

    override fun setMoreProducts(products: List<ProductUIModel>) {
        runOnUiThread {
            binding.mainSkeleton.isVisible = false
            binding.rvProducts.isVisible = true
            adapter.addList(products)
        }
    }

    override fun setRecentProducts(recentProductsData: List<RecentProductUIModel>) {
        runOnUiThread {
            adapter.updateRecentProducts(recentProductsData)
        }
    }

    override fun setCartProducts(cartCounts: Map<Int, Int>) {
        runOnUiThread {
            adapter.updateCartCounts(cartCounts)
        }
    }

    override fun navigateToProductDetail(productId: Int) {
        startActivity(DetailedProductActivity.getIntent(this, productId))
    }

    override fun setToolbar(totalCount: Int) {
        runOnUiThread {
            tvCount?.text = totalCount.toString()
        }
    }

    private fun navigateToCart() {
        startActivity(CartActivity.getIntent(this))
    }

    override fun navigateToOrderHistories() {
        startActivity(OrderHistoriesActivity.getIntent(this))
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ShoppingActivity::class.java)
        }
    }
}
