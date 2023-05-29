package woowacourse.shopping.feature.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import woowacourse.shopping.data.repository.remote.ProductRepositoryImpl
import woowacourse.shopping.data.service.CartProductRemoteService
import woowacourse.shopping.data.service.ProductRemoteService
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initAdapters()
        initLayoutManager()
        binding.productRecyclerView.adapter = concatAdapter

        initPresenter()
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

    private fun initPresenter() {
        val productRemoteService = ProductRemoteService()
        val cartProductRemoteService = CartProductRemoteService()
        presenter = MainPresenter(
            ProductRepositoryImpl(productRemoteService),
            CartRepositoryImpl(cartProductRemoteService),
            RecentProductRepositoryImpl(RecentDao(this)),
        )
    }

    private fun observePresenter() {
        presenter.badgeCount.observe(this) { cartCountBadge?.count = it }
        presenter.products.observe(this) { mainProductAdapter.setItems(it) }
        presenter.recentProducts.observe(this) { recentAdapter.setItems(it) }
        presenter.mainScreenEvent.observe(this) { handleMainScreenEvent(it) }
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
        presenter.initLoadData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)

        cartCountBadge =
            menu.findItem(R.id.cart_count_badge).actionView?.findViewById(R.id.badge)

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
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        recentWrapperAdapter.onRestoreState(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            presenter.resetProducts()
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, MainActivity::class.java)

        private const val TOTAL_SPAN = 2
        private const val HALF_SPAN = TOTAL_SPAN / 2
    }
}
