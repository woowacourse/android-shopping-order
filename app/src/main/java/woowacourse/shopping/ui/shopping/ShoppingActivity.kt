package woowacourse.shopping.ui.shopping

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.Storage
import woowacourse.shopping.common.utils.Toaster
import woowacourse.shopping.common.utils.convertDpToPixel
import woowacourse.shopping.databinding.ActivityShoppingBinding
import woowacourse.shopping.ui.DatabaseInjector
import woowacourse.shopping.ui.RepositoryInjector
import woowacourse.shopping.ui.RetrofitInjector
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.model.ProductModel
import woowacourse.shopping.ui.model.RecentProductModel
import woowacourse.shopping.ui.model.ShoppingProductModel
import woowacourse.shopping.ui.orderhistory.OrderHistoryActivity
import woowacourse.shopping.ui.productdetail.ProductDetailActivity
import woowacourse.shopping.ui.shopping.recyclerview.LoadMoreAdapter
import woowacourse.shopping.ui.shopping.recyclerview.ProductAdapter
import woowacourse.shopping.ui.shopping.recyclerview.RecentProductAdapter
import woowacourse.shopping.ui.shopping.recyclerview.RecentProductWrapperAdapter

class ShoppingActivity : AppCompatActivity(), ShoppingContract.View {
    private lateinit var binding: ActivityShoppingBinding
    private lateinit var presenter: ShoppingContract.Presenter
    private var shoppingCartAmount: TextView? = null
    private var memberPoints: MenuItem? = null

    private val productAdapter: ProductAdapter by lazy {
        ProductAdapter(
            onProductItemClick = { presenter.openProduct(it.product) },
            onMinusAmountButtonClick = { presenter.decreaseCartProductAmount(it) },
            onPlusAmountButtonClick = { presenter.increaseCartProductAmount(it) }
        )
    }

    private val recentProductAdapter: RecentProductAdapter by lazy {
        RecentProductAdapter(emptyList())
    }

    private val recentProductWrapperAdapter: RecentProductWrapperAdapter by lazy {
        RecentProductWrapperAdapter(recentProductAdapter)
    }

    private val loadMoreAdapter: LoadMoreAdapter by lazy {
        LoadMoreAdapter {
            presenter.loadProductsInSize()
        }
    }

    private val concatAdapter: ConcatAdapter by lazy {
        val config = ConcatAdapter.Config.Builder().apply {
            setIsolateViewTypes(false)
        }.build()
        ConcatAdapter(
            config, recentProductWrapperAdapter, productAdapter, loadMoreAdapter
        )
    }

    private val gridItemDecoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) - 1
            val column = position % SPAN_COUNT
            val density = resources.displayMetrics.density

            if (position in 0 until SPAN_COUNT) {
                outRect.top += convertDpToPixel(DP_GRID_TOP_OFFSET, density)
            }

            val edgeHorizontalOffset = convertDpToPixel(DP_GRID_EDGE_HORIZONTAL_OFFSET, density)
            if (column == 0) {
                outRect.left += edgeHorizontalOffset
            } else if (column == SPAN_COUNT - 1) {
                outRect.right += edgeHorizontalOffset
            }
        }
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            beforeLoad()
            presenter.reloadProducts()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.shopping_toolbar))

        binding.shoppingProductList.itemAnimator = null

        beforeLoad()
        initProductList()

        initPresenter()
    }

    override fun onResume() {
        super.onResume()
        presenter.updateRecentProducts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_shopping, menu)
        val shoppingCartAction = menu?.findItem(R.id.shopping_cart_action)
        shoppingCartAction?.actionView?.setOnClickListener {
            onOptionsItemSelected(shoppingCartAction)
        }
        shoppingCartAmount =
            shoppingCartAction?.actionView?.findViewById(R.id.tv_shopping_cart_amount)

        memberPoints = menu?.findItem(R.id.member_points)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        setupMenuView()
        return super.onPrepareOptionsMenu(menu)
    }

    private fun setupMenuView() {
        presenter.loadPoints()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shopping_cart_action -> presenter.openCart()
            R.id.order_history_action -> presenter.openOrderHistory()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun beforeLoad() {
        showSkeleton()
    }

    private fun showSkeleton() {
        binding.shoppingProductList.visibility = View.GONE
        binding.skeletonShoppingProductList.visibility = View.VISIBLE
    }

    override fun afterLoad() {
        showProducts()
    }

    private fun showProducts() {
        binding.skeletonShoppingProductList.visibility = View.GONE
        binding.shoppingProductList.visibility = View.VISIBLE
    }

    override fun updateProducts(productModels: List<ShoppingProductModel>) {
        productAdapter.updateProducts(productModels)
    }

    override fun addProducts(productModels: List<ShoppingProductModel>) {
        productAdapter.addProducts(productModels)
    }

    override fun updateRecentProducts(recentProductModels: List<RecentProductModel>) {
        recentProductAdapter.updateRecentProducts(recentProductModels)
        recentProductWrapperAdapter.updateRecentProduct()
    }

    override fun showProductDetail(productModel: ProductModel, recentProductModel: ProductModel?) {
        startProductDetailActivity(productModel, recentProductModel)
    }

    private fun startProductDetailActivity(
        productModel: ProductModel,
        recentProductModel: ProductModel?
    ) {
        val intent = ProductDetailActivity.createIntent(this, productModel, recentProductModel)
        activityResultLauncher.launch(intent)
    }

    override fun showCart() {
        startCartActivity()
    }

    private fun startCartActivity() {
        val intent = CartActivity.createIntent(this)
        activityResultLauncher.launch(intent)
    }

    override fun updateCartQuantity(amount: Int) {
        shoppingCartAmount?.text = amount.toString()
    }

    override fun updateShoppingProduct(shoppingProductModel: ShoppingProductModel) {
        productAdapter.updateProduct(shoppingProductModel)
    }

    override fun notifyFailure(message: String) {
        Toaster.showToast(this, message)
    }

    override fun showPoints(points: Int) {
        val spannableString = SpannableString(getString(R.string.points, points))
        spannableString.setSpan(
            ForegroundColorSpan(getColor(R.color.primary)),
            0,
            spannableString.length,
            0
        )
        spannableString.setSpan(AbsoluteSizeSpan(50), 0, spannableString.length, 0)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, spannableString.length, 0)
        memberPoints?.title = spannableString
    }

    override fun showOrderHistory() {
        val intent = OrderHistoryActivity.createIntent(this)
        startActivity(intent)
    }


    private fun initProductList() {
        binding.shoppingProductList.layoutManager = makeLayoutManager()
        binding.shoppingProductList.addItemDecoration(gridItemDecoration)
        binding.shoppingProductList.adapter = concatAdapter
    }

    private fun initPresenter() {
        val database = DatabaseInjector.inject(this)
        val server = Storage.getInstance(this).server
        val retrofit = RetrofitInjector.inject(this)
        val productRepository = RepositoryInjector.injectProductRepository(retrofit)
        val recentProductRepository =
            RepositoryInjector.injectRecentProductRepository(database, server, retrofit)
        val cartRepository = RepositoryInjector.injectCartRepository(retrofit)
        val memberRepository = RepositoryInjector.injectMemberRepository(retrofit)

        presenter = ShoppingPresenter(
            this,
            productRepository,
            recentProductRepository,
            cartRepository,
            memberRepository,
            recentProductSize = 10,
            productLoadSize = 20
        )
    }

    private fun makeLayoutManager(): GridLayoutManager {
        return GridLayoutManager(this, SPAN_COUNT).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (concatAdapter.getItemViewType(position)) {
                        ProductAdapter.VIEW_TYPE -> 1
                        RecentProductWrapperAdapter.VIEW_TYPE -> 2
                        else -> 2
                    }
                }
            }
        }
    }

    companion object {
        private const val SPAN_COUNT = 2
        private const val DP_GRID_TOP_OFFSET = 10
        private const val DP_GRID_EDGE_HORIZONTAL_OFFSET = 14

        fun createIntent(context: Context): Intent {
            return Intent(context, ShoppingActivity::class.java)
        }
    }
}
