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
import woowacourse.shopping.data.localDataSource.CartDefaultLocalDataSource
import woowacourse.shopping.data.remoteDataSourceImpl.CartRemoteDataSourceImpl
import woowacourse.shopping.data.remoteDataSourceImpl.ProductRemoteDataSourceImpl
import woowacourse.shopping.data.repositoryImpl.CartRepositoryImpl
import woowacourse.shopping.data.repositoryImpl.ProductRepositoryImpl
import woowacourse.shopping.data.repositoryImpl.RecentRepositoryImpl
import woowacourse.shopping.database.product.ProductSqliteDataSource
import woowacourse.shopping.database.recentProduct.RecentSqliteProductDataSource
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
        presenter.setUpRecentProducts()
        presenter.setUpCartCounts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cart_menu, menu)
        menu?.findItem(R.id.cart)?.actionView?.let { view ->
            view.setOnClickListener { navigateToCart() }
            view.findViewById<TextView>(R.id.tv_counter)?.let { tvCount = it }
        }
        presenter.setUpTotalCount()
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
            productRepository = ProductRepositoryImpl(
                localDataSource = ProductSqliteDataSource(this),
                remoteDataSource = ProductRemoteDataSourceImpl()
            ),
            cartRepository = CartRepositoryImpl(
                localDataSource = CartDefaultLocalDataSource(),
                remoteDataSource = CartRemoteDataSourceImpl()
            ),
            recentRepository = RecentRepositoryImpl(
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
        presenter.setUpRecentProducts()
        presenter.setUpNextProducts()
        presenter.setUpCartCounts()

        binding.rvProducts.adapter = adapter
        binding.rvProducts.itemAnimator = null
        binding.bottom.navigateToOrders = presenter::navigateToOrders
    }

    private fun getAdapterListener() = object : ProductsListener {
        override fun onClickItem(productId: Int) {
            presenter.navigateToItemDetail(productId)
        }
        override fun onReadMoreClick() {
            presenter.setUpNextProducts()
        }
        override fun onAddCartOrUpdateCount(productId: Int, count: Int) {
            presenter.updateItemCount(productId, count)
            presenter.setUpTotalCount()
        }
    }

    override fun addMoreProducts(products: List<ProductUIModel>) {
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

    override fun navigateToOrders() {
        startActivity(OrderHistoriesActivity.getIntent(this))
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ShoppingActivity::class.java)
        }
    }
}
