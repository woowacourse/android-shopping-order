package woowacourse.shopping.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.app.ShoppingApplication.Companion.localRecentDataSource
import woowacourse.shopping.app.ShoppingApplication.Companion.remoteCartDataSource
import woowacourse.shopping.app.ShoppingApplication.Companion.remoteProductDataSource
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityHomeBinding
import woowacourse.shopping.ui.detail.DetailActivity
import woowacourse.shopping.ui.home.action.HomeNavigationActions
import woowacourse.shopping.ui.home.adapter.product.HomeViewItem
import woowacourse.shopping.ui.home.adapter.product.HomeViewItem.Companion.LOAD_MORE_BUTTON_VIEW_TYPE
import woowacourse.shopping.ui.home.adapter.product.ProductAdapter
import woowacourse.shopping.ui.home.adapter.recent.RecentProductAdapter
import woowacourse.shopping.ui.home.viewmodel.HomeViewModel
import woowacourse.shopping.ui.home.viewmodel.HomeViewModelFactory
import woowacourse.shopping.ui.order.OrderActivity
import woowacourse.shopping.ui.state.UiState

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recentProductAdapter: RecentProductAdapter
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            ProductRepositoryImpl(remoteProductDataSource),
            CartRepositoryImpl(remoteCartDataSource),
            RecentProductRepositoryImpl(localRecentDataSource),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpAdapter()
        setUpDataBinding()
        observeViewModel()
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.updateData()
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun setUpAdapter() {
        productAdapter = ProductAdapter(viewModel)
        binding.rvProductList.adapter = productAdapter
        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (productAdapter.getItemViewType(position) == LOAD_MORE_BUTTON_VIEW_TYPE) return 2
                    return 1
                }
            }
        binding.rvProductList.layoutManager = layoutManager

        recentProductAdapter = RecentProductAdapter(viewModel)
        binding.rvRecentProducts.adapter = recentProductAdapter
    }

    private fun observeViewModel() {
        viewModel.homeUiState.observe(this) { state ->
            when (state) {
                is UiState.Success -> showData(state.data)
                is UiState.Loading -> showData(List(20) { HomeViewItem.ProductPlaceHolderViewItem() })
                is UiState.Error ->
                    showError(state.exception.message ?: getString(R.string.unknown_error))
            }
        }

        viewModel.recentProducts.observe(this) { recentProducts ->
            recentProductAdapter.submitList(recentProducts)
        }

        viewModel.homeNavigationActions.observe(this) { homeNavigationActions ->
            homeNavigationActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is HomeNavigationActions.NavigateToDetail -> navigateToDetail(action.productId)
                    is HomeNavigationActions.NavigateToCart -> navigateToCart()
                }
            }
        }
    }

    private fun showData(data: List<HomeViewItem>) {
        productAdapter.submitProductViewItems(data.toList(), viewModel.canLoadMore.value ?: false)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToDetail(productId: Int) {
        startActivity(DetailActivity.createIntent(this, productId))
    }

    private fun navigateToCart() {
        startActivity(OrderActivity.createIntent(this))
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }
}
