package woowacourse.shopping.feature.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.common_ui.CartCounterBadge
import woowacourse.shopping.data.repository.local.CartRepositoryImpl
import woowacourse.shopping.data.repository.local.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.remote.MockRemoteProductRepositoryImpl
import woowacourse.shopping.data.service.MockProductRemoteService
import woowacourse.shopping.data.sql.cart.CartDao
import woowacourse.shopping.data.sql.recent.RecentDao
import woowacourse.shopping.databinding.ActivityMainBinding
import woowacourse.shopping.feature.cart.CartActivity
import woowacourse.shopping.feature.detail.DetailActivity
import woowacourse.shopping.feature.main.load.LoadAdapter
import woowacourse.shopping.feature.main.product.MainProductAdapter
import woowacourse.shopping.feature.main.product.ProductClickListener
import woowacourse.shopping.feature.main.recent.RecentAdapter
import woowacourse.shopping.feature.main.recent.RecentProductClickListener
import woowacourse.shopping.feature.main.recent.RecentWrapperAdapter
import woowacourse.shopping.util.getParcelableCompat
import woowacourse.shopping.util.keyError

class MainActivity : AppCompatActivity(), MainContract.View {
    lateinit var binding: ActivityMainBinding
    private lateinit var presenter: MainContract.Presenter
    private lateinit var mainProductAdapter: MainProductAdapter
    private lateinit var recentAdapter: RecentAdapter
    private lateinit var recentWrapperAdapter: RecentWrapperAdapter
    private lateinit var loadAdapter: LoadAdapter

    private var cartCountBadge: CartCounterBadge? = null

    private val concatAdapter: ConcatAdapter by lazy {
        val config = ConcatAdapter.Config.Builder().apply {
            setIsolateViewTypes(false)
        }.build()
        ConcatAdapter(config, recentWrapperAdapter, mainProductAdapter, loadAdapter)
    }

    private val recentProductClickListener: RecentProductClickListener =
        object : RecentProductClickListener {
            override fun onClick(productId: Long) {
                presenter.showRecentProductDetail(productId)
            }
        }

    private val productClickListener: ProductClickListener = object : ProductClickListener {
        override fun onClick(productId: Long) {
            presenter.showProductDetail(productId)
        }

        override fun onCartCountChanged(productId: Long, count: Int) {
            presenter.changeProductCartCount(productId, count)
        }
    }

    // 비동기적으로 아이템을 얻어오기 때문에 뷰시스템의 리사이클러뷰 스크롤 상태에 대한 자동 복구 기능이 정상 작동하지 않음
    private var isFirstLoad: Boolean = false
    private var recyclerViewState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val serverUrl =
            intent.getStringExtra(SERVER_URL_KEY) ?: return keyError(SERVER_URL_KEY)

        initAdapters()
        initLayoutManager()
        binding.productRecyclerView.adapter = concatAdapter

        initPresenter(serverUrl)
        observePresenter()
    }

    private fun initAdapters() {
        mainProductAdapter = MainProductAdapter(productClickListener)
        recentAdapter = RecentAdapter(recentProductClickListener)
        recentWrapperAdapter = RecentWrapperAdapter(recentAdapter)
        loadAdapter = LoadAdapter { presenter.loadMoreProduct() }
    }

    private fun initLayoutManager() {
        val layoutManager = GridLayoutManager(this, TOTAL_SPAN)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (concatAdapter.getItemViewType(position)) {
                    LoadAdapter.VIEW_TYPE, RecentWrapperAdapter.VIEW_TYPE -> TOTAL_SPAN
                    MainProductAdapter.VIEW_TYPE -> HALF_SPAN
                    else -> TOTAL_SPAN
                }
            }
        }
        binding.productRecyclerView.layoutManager = layoutManager
    }

    private fun initPresenter(serverKey: String) {
        presenter = MainPresenter(
            MockRemoteProductRepositoryImpl(MockProductRemoteService(serverKey)),
            CartRepositoryImpl(CartDao(this)),
            RecentProductRepositoryImpl(RecentDao(this)),
        )
    }

    private fun observePresenter() {
        presenter.badgeCount.observe(this) { cartCountBadge?.count = it }
        presenter.products.observe(this) {
            if (isFirstLoad.not()) {
                restoreProductRecyclerViewState()
                isFirstLoad = true
            }
            mainProductAdapter.setItems(it)
        }
        presenter.recentProducts.observe(this) { recentAdapter.setItems(it) }
        presenter.mainScreenEvent.observe(this) {
            handleMainScreenEvent(it)
        }
    }

    private fun handleMainScreenEvent(event: MainContract.View.MainScreenEvent) {
        when (event) {
            is MainContract.View.MainScreenEvent.ShowCartScreen -> {
                startActivity(CartActivity.getIntent(this))
            }
            is MainContract.View.MainScreenEvent.ShowProductDetailScreen -> {
                startActivity(DetailActivity.getIntent(this, event.product, event.recentProduct))
            }
            is MainContract.View.MainScreenEvent.HideLoadMore -> {
                hideLoadMore()
            }
            is MainContract.View.MainScreenEvent.ShowLoading -> {
                binding.skeletonMainLoadingLayout.visibility = View.VISIBLE
                binding.productRecyclerView.visibility = View.GONE
            }
            is MainContract.View.MainScreenEvent.HideLoading -> {
                binding.skeletonMainLoadingLayout.visibility = View.GONE
                binding.productRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun hideLoadMore() {
        Toast.makeText(this, getString(R.string.load_more_end), Toast.LENGTH_SHORT).show()
        loadAdapter.hide()
    }

    override fun onStart() {
        super.onStart()
        presenter.loadProducts()
        presenter.loadRecent()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)

        cartCountBadge =
            menu.findItem(R.id.cart_count_badge).actionView?.findViewById(R.id.badge)

        presenter.loadCartCountSize()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.cart_action -> {
                presenter.moveToCart()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        recentWrapperAdapter.onSaveState(outState)
        outState.putParcelable(
            RECYCLER_VIEW_STATE_KEY,
            binding.productRecyclerView.layoutManager?.onSaveInstanceState(),
        )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        recentWrapperAdapter.onRestoreState(savedInstanceState)
        recyclerViewState = savedInstanceState.getParcelableCompat(RECYCLER_VIEW_STATE_KEY)

        // 혹시 비동기로 얻어오는게 리사이클러뷰 상태를 복구해서 얻어오는 것보다 빠를 경우를 위해
        if (isFirstLoad) restoreProductRecyclerViewState()
    }

    private fun restoreProductRecyclerViewState() {
        binding.productRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            presenter.resetProducts()
        }
    }

    companion object {
        fun getIntent(context: Context, serverUrl: String): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(SERVER_URL_KEY, serverUrl)
            return intent
        }

        private const val SERVER_URL_KEY = "server_url_key"

        private const val RECYCLER_VIEW_STATE_KEY = "recycler_view_state_key"

        private const val TOTAL_SPAN = 2
        private const val HALF_SPAN = TOTAL_SPAN / 2
    }
}
