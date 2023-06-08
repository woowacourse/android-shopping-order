package woowacourse.shopping.presentation.productlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.common.PreferenceUtil
import woowacourse.shopping.data.local.recentproduct.RecentProductDao
import woowacourse.shopping.data.local.recentproduct.RecentProductDbHelper
import woowacourse.shopping.data.remote.cart.CartRemoteDataSource
import woowacourse.shopping.data.remote.cart.CartRepositoryImpl
import woowacourse.shopping.data.remote.product.ProductDataSource
import woowacourse.shopping.data.remote.product.ProductRemoteDataSource
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityProductListBinding
import woowacourse.shopping.databinding.BadgeCartBinding
import woowacourse.shopping.presentation.cart.CartActivity
import woowacourse.shopping.presentation.model.CartProductListModel
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.orderlist.OrderListActivity
import woowacourse.shopping.presentation.productdetail.ProductDetailActivity
import woowacourse.shopping.presentation.productlist.product.ProductListAdapter
import woowacourse.shopping.presentation.productlist.recentproduct.RecentProductAdapter

class ProductListActivity : AppCompatActivity(), ProductListContract.View {
    private lateinit var activityBinding: ActivityProductListBinding
    private var cartIconBinding: BadgeCartBinding? = null

    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var recentProductAdapter: RecentProductAdapter
    private lateinit var cartMenuItem: MenuItem
    private lateinit var orderListMenuItem: MenuItem

    private val productDataSource: ProductDataSource by lazy { ProductRemoteDataSource }
    private val presenter: ProductListPresenter by lazy {
        ProductListPresenter(
            this,
            ProductRepositoryImpl(productDataSource),
            RecentProductRepositoryImpl(
                RecentProductDao(RecentProductDbHelper(this)),
                ProductRemoteDataSource,
            ),
            CartRepositoryImpl(CartRemoteDataSource(PreferenceUtil(this))),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)
        initView()
    }

    override fun onStart() {
        super.onStart()
        setLoadingViewVisible(true)
        updateView()
    }

    private fun initView() {
        setSupportActionBar(activityBinding.toolbarProductList.toolbar)
        initRecentProductAdapter()
        initProductAdapter()
    }

    private fun updateView() {
        presenter.refreshProductItems()
        presenter.loadRecentProductItems()
        presenter.updateCartCount()
    }

    private fun initRecentProductAdapter() {
        recentProductAdapter = RecentProductAdapter(::productClick)
    }

    private fun productClick(productModel: ProductModel) {
        showProductDetail(productModel)
    }

    private fun showProductDetail(productModel: ProductModel) {
        startActivity(ProductDetailActivity.getIntent(this, productModel))
    }

    private fun initProductAdapter() {
        productListAdapter = ProductListAdapter(
            recentProductAdapter = recentProductAdapter,
            presenter = presenter,
        )

        val layoutManager = GridLayoutManager(this, SPAN_COUNT)
        activityBinding.recyclerProduct.layoutManager = layoutManager.apply {
            spanSizeLookup = ProductListSpanSizeLookup(productListAdapter::getItemViewType)
        }

        activityBinding.recyclerProduct.adapter = productListAdapter
    }

    override fun loadProductItems(cartProductModels: List<CartProductModel>) {
        val newList =
            listOf(CartProductModel.defaultInfo()) + cartProductModels + listOf(
                CartProductModel.defaultInfo(),
            )
        productListAdapter.submitList(newList)
    }

    override fun loadRecentProductItems(productModels: List<ProductModel>) {
        recentProductAdapter.submitList(productModels)
    }

    override fun showCartCount(count: Int) {
        cartIconBinding?.badgeCartCounter?.text = count.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        cartIconBinding = BadgeCartBinding.inflate(layoutInflater, null, false)
        initToolbarIcon(menu)
        return true
    }

    private fun initToolbarIcon(menu: Menu) {
        menuInflater.inflate(R.menu.menu_product_list_toolbar, menu)
        cartMenuItem = menu.findItem(R.id.icon_cart)
        orderListMenuItem = menu.findItem(R.id.icon_order)
        setUpCartIconBinding()
        setOnOrderListClickEvent()
    }

    private fun setOnOrderListClickEvent() {
        orderListMenuItem.setOnMenuItemClickListener {
            startActivity(Intent(this, OrderListActivity::class.java))
            return@setOnMenuItemClickListener true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun setUpCartIconBinding() {
        cartIconBinding =
            BadgeCartBinding.inflate(
                LayoutInflater.from(this),
                null,
                false,
            )
        cartMenuItem.actionView = cartIconBinding?.root
        cartIconBinding?.iconCartMenu?.setOnClickListener {
            presenter.showMyCart()
        }
        presenter.updateCartCount()
    }

    override fun navigateToCart(cartProductModels: List<CartProductModel>) {
        startActivity(CartActivity.getIntent(this, CartProductListModel(cartProductModels)))
    }

    override fun setLoadingViewVisible(isVisible: Boolean) {
        if (isVisible) {
            activityBinding.flProductList.visibility = View.VISIBLE
            activityBinding.recyclerProduct.visibility = View.GONE
        } else {
            activityBinding.recyclerProduct.visibility = View.VISIBLE
            activityBinding.flProductList.visibility = View.GONE
        }
    }

    override fun showErrorForServerView() {
        Toast.makeText(this, getString(R.string.error_server), Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val SPAN_COUNT = 2
        fun getIntent(context: Context): Intent {
            return Intent(context, ProductListActivity::class.java)
        }
    }
}
