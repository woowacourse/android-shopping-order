package woowacourse.shopping.ui.shopping

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityShoppingBinding
import woowacourse.shopping.model.CartProductModel
import woowacourse.shopping.model.ProductCountModel
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.model.RecentProductModel
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.orderhistory.OrderHistoryActivity
import woowacourse.shopping.ui.productdetail.ProductDetailActivity
import woowacourse.shopping.ui.shopping.ShoppingContract.Presenter
import woowacourse.shopping.ui.shopping.ShoppingContract.View
import woowacourse.shopping.ui.shopping.recyclerview.adapter.loadmore.LoadMoreAdapter
import woowacourse.shopping.ui.shopping.recyclerview.adapter.product.ProductAdapter
import woowacourse.shopping.ui.shopping.recyclerview.adapter.recentproduct.RecentProductAdapter
import woowacourse.shopping.ui.shopping.recyclerview.adapter.recentproduct.RecentProductWrapperAdapter
import woowacourse.shopping.util.builder.add
import woowacourse.shopping.util.builder.notIsolatedViewTypeConcatAdapter
import woowacourse.shopping.util.extension.findItemActionView
import woowacourse.shopping.util.extension.findTextView
import woowacourse.shopping.util.extension.getParcelableExtraCompat
import woowacourse.shopping.util.extension.setContentView
import woowacourse.shopping.util.extension.showToast
import woowacourse.shopping.util.inject.injectShoppingPresenter
import woowacourse.shopping.util.listener.CartProductClickListener
import woowacourse.shopping.widget.SkeletonCounterView

class ShoppingActivity : AppCompatActivity(), View,
    SkeletonCounterView.OnCountChangedListener, CartProductClickListener {
    private lateinit var binding: ActivityShoppingBinding
    private val presenter: Presenter by lazy {
        injectShoppingPresenter(view = this, context = this)
    }

    private val recentProductAdapter = RecentProductAdapter(presenter::inquiryRecentProductDetail)
    private val recentProductWrapperAdapter = RecentProductWrapperAdapter(recentProductAdapter)
    private val productAdapter = ProductAdapter(this, this)
    private val loadMoreAdapter = LoadMoreAdapter(presenter::loadMoreProducts)

    private val cartActivityLauncher = registerForActivityResult(StartActivityForResult()) {
        presenter.fetchAll()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingBinding.inflate(layoutInflater).setContentView(this)
        initShoppingToolbar()
        initView()
    }

    private fun initShoppingToolbar() {
        val cart = binding.shoppingToolBar.findItemActionView(R.id.cart)
        cart?.setOnClickListener { presenter.inquiryCart() }

        binding.shoppingToolBar.setOnMenuItemClickListener { item ->
            if (item?.itemId == R.id.order_list) presenter.inquiryOrders()
            true
        }
    }

    private fun initView() {
        binding.presenter = presenter
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.adapter = notIsolatedViewTypeConcatAdapter {
            add(recentProductWrapperAdapter)
            add(productAdapter)
            add(loadMoreAdapter)
        }
    }

    override fun updateProducts(products: List<CartProductModel>) {
        productAdapter.submitList(products)
    }

    override fun updateRecentProducts(recentProducts: List<RecentProductModel>) {
        recentProductWrapperAdapter.submitList(recentProducts)
    }

    override fun navigateToProductDetail(product: ProductModel) {
        startActivity(ProductDetailActivity.getIntent(this, product, true))
    }

    override fun navigateToCart() {
        cartActivityLauncher.launch(CartActivity.getIntent(this))
    }

    override fun showLoadMoreButton() {
        loadMoreAdapter.showButton()
    }

    override fun hideLoadMoreButton() {
        loadMoreAdapter.hideButton()
    }

    override fun showProductLoadFailed() {
        showToast(getString(R.string.product_load_failed))
    }

    override fun navigateToOrderList() {
        startActivity(OrderHistoryActivity.getIntent(this))
    }

    override fun showCartProductSaveFailed() {
        showToast(getString(R.string.cart_product_save_failed))
    }

    override fun showCartCountChangedFailed() {
        showToast(getString(R.string.cart_product_count_change_failed))
    }

    override fun updateCartBadge(count: ProductCountModel) {
        val cartBadgeView = binding.shoppingToolBar.findItemActionView(R.id.cart) ?: return
        val productCountTextView = cartBadgeView.findTextView(R.id.cart_count_badge) ?: return

        productCountTextView.visibility = count.getVisibility()
        productCountTextView.text = count.toString()
    }

    override fun onClickCartProduct(cartProduct: CartProductModel) {
        presenter.inquiryProductDetail(cartProduct)
    }

    override fun onAddCartProduct(cartProduct: CartProductModel) {
        presenter.addCartProduct(cartProduct.product)
    }

    override fun onCountChanged(cartProduct: CartProductModel, changedCount: Int) {
        presenter.updateCartCount(cartProduct, changedCount)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val product = intent?.getParcelableExtraCompat<ProductModel>(CART_PRODUCT_KEY) ?: return
        val count = intent.getIntExtra(COUNT_KEY, 0)
        presenter.increaseCartCount(product, count)
    }

    companion object {
        private const val CART_PRODUCT_KEY = "product_key"
        private const val COUNT_KEY = "count_key"
        private const val SERVER_URL_KEY = "server_url_key"

        fun getIntent(context: Context, product: ProductModel, count: Int): Intent =
            Intent(context, ShoppingActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .putExtra(CART_PRODUCT_KEY, product)
                .putExtra(COUNT_KEY, count)

        fun getIntent(context: Context, serverUrl: String): Intent =
            Intent(context, ShoppingActivity::class.java)
                .putExtra(SERVER_URL_KEY, serverUrl)
    }
}