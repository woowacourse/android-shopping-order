package woowacourse.shopping.view.home

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication.Companion.recentProductDatabase
import woowacourse.shopping.ShoppingApplication.Companion.remoteCartDataSource
import woowacourse.shopping.ShoppingApplication.Companion.remoteProductDataSource
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityHomeBinding
import woowacourse.shopping.view.cart.CartActivity
import woowacourse.shopping.view.detail.DetailActivity
import woowacourse.shopping.view.home.product.HomeViewItem
import woowacourse.shopping.view.home.product.HomeViewItem.Companion.LOAD_MORE_BUTTON_VIEW_TYPE
import woowacourse.shopping.view.home.product.HomeViewItem.ProductViewItem
import woowacourse.shopping.view.home.product.ProductAdapter
import woowacourse.shopping.view.home.recent.RecentProductAdapter
import woowacourse.shopping.view.state.HomeUiEvent
import woowacourse.shopping.view.state.UIState

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recentProductAdapter: RecentProductAdapter
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            ProductRepositoryImpl(remoteProductDataSource),
            CartRepositoryImpl(remoteCartDataSource),
            RecentProductRepositoryImpl(recentProductDatabase),
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
        viewModel.loadRecentItems()
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun setUpAdapter() {
        productAdapter = ProductAdapter(viewModel, viewModel)
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
        binding.rvProductList.itemAnimator = null
        binding.rvRecentProducts.itemAnimator = null
    }

    private fun observeViewModel() {
        viewModel.recentProductUiState.observe(this) { recentProductState ->
            if (!recentProductState.isLoading) {
                recentProductAdapter.loadData(recentProductState.productItems)
            }
        }

        viewModel.homeProductUiState.observe(this) { homeProductState ->
            if (!homeProductState.isLoading) {
                showData(homeProductState.productItems)
            }
        }

        viewModel.homeUiEvent.observe(this) { homeUiEvent ->
            when (val event = homeUiEvent.getContentIfNotHandled() ?: return@observe) {
                is HomeUiEvent.NavigateToCart -> navigateToCart()
                is HomeUiEvent.NavigateToDetail -> navigateToDetail(event.productId)
            }
        }
    }

    private fun showData(data: List<ProductViewItem>) {
        productAdapter.loadData(data, viewModel.homeProductUiState.value?.loadMoreAvailable ?: false)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun navigateToDetail(productId: Int) {
        startActivity(DetailActivity.createIntent(this, productId))
    }

    private fun navigateToCart() {
        startActivity(CartActivity.createIntent(this))
    }
}
