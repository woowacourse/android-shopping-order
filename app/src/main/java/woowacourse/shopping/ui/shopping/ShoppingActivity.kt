package woowacourse.shopping.ui.shopping

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.dataSource.RemoteCartDataSource
import woowacourse.shopping.data.dataSource.RemoteProductDataSource
import woowacourse.shopping.data.database.recentProduct.RecentProductDatabase
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityShoppingBinding
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.detailedProduct.DetailedProductActivity
import woowacourse.shopping.ui.orderlist.OrderListActivity
import woowacourse.shopping.ui.shopping.productAdapter.ProductsAdapter
import woowacourse.shopping.ui.shopping.productAdapter.ProductsAdapterDecoration.getItemDecoration
import woowacourse.shopping.ui.shopping.productAdapter.ProductsAdapterDecoration.getSpanSizeLookup
import woowacourse.shopping.ui.shopping.productAdapter.ProductsListener
import woowacourse.shopping.uimodel.ProductUIModel
import woowacourse.shopping.uimodel.RecentProductUIModel

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
    }

    override fun onResume() {
        super.onResume()
        presenter.setUpRecentProducts()
        presenter.setUpCartCounts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shopping_menu, menu)
        menu?.findItem(R.id.cart)?.actionView?.let { view ->
            view.setOnClickListener { navigateToCart() }
            view.findViewById<TextView>(R.id.tv_counter)?.let { tvCount = it }
        }
        presenter.setUpTotalCount()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.order -> {
                navigateToOrderList()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shopping)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.itemAnimator = null
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun initPresenter() {
        presenter = ShoppingPresenter(
            this,
            ProductRepositoryImpl(
                RemoteProductDataSource(),
            ),
            RecentProductDatabase(this),
            CartRepositoryImpl(RemoteCartDataSource()),
        )
        presenter.setUpRecentProducts()
        presenter.setUpNextProducts()
        presenter.setUpCartCounts()
    }

    private fun initLayoutManager() {
        val layoutManager = binding.rvProducts.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = getSpanSizeLookup(layoutManager, adapter)
        binding.rvProducts.addItemDecoration(getItemDecoration(layoutManager, resources))
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
        binding.mainSkeleton.isVisible = false
        binding.rvProducts.isVisible = true
        adapter.addList(products)
    }

    override fun setRecentProducts(recentProductsData: List<RecentProductUIModel>) {
        adapter.updateRecentProducts(recentProductsData)
    }

    override fun setCartProducts(cartCounts: Map<Int, Int>) {
        adapter.updateCartCounts(cartCounts)
    }

    override fun navigateToProductDetail(product: ProductUIModel) {
        startActivity(DetailedProductActivity.getIntent(this, product))
    }

    override fun setToolbar(totalCount: Int) {
        tvCount?.text = totalCount.toString()
    }

    private fun navigateToCart() {
        startActivity(CartActivity.getIntent(this))
    }

    private fun navigateToOrderList() {
        startActivity(OrderListActivity.getIntent(this))
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ShoppingActivity::class.java)
        }
    }
}
