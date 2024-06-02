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
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.Companion.LOAD_MORE_BUTTON_VIEW_TYPE
import woowacourse.shopping.view.home.adapter.product.HomeViewItem.ProductViewItem
import woowacourse.shopping.view.home.adapter.product.ProductAdapter
import woowacourse.shopping.view.home.adapter.recent.RecentProductAdapter
import woowacourse.shopping.view.home.viewmodel.HomeViewModel
import woowacourse.shopping.view.home.viewmodel.HomeViewModelFactory
import woowacourse.shopping.view.state.UiState

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
                is UiState.Loading -> showData(emptyList())
                is UiState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
            }
        }

        viewModel.recentProducts.observe(this) { recentProducts ->
            recentProductAdapter.submitList(recentProducts)
        }

        viewModel.navigateToDetail.observe(this) { navigateToDetail ->
            navigateToDetail.getContentIfNotHandled()?.let { productId ->
                navigateToDetail(productId)
            }
        }

        viewModel.navigateToCart.observe(this) { navigateToCart ->
            navigateToCart.getContentIfNotHandled()?.let {
                navigateToCart()
            }
        }
    }

    private fun showData(data: List<ProductViewItem>) {
        productAdapter.submitProductViewItems(data, viewModel.canLoadMore.value ?: false)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToDetail(productId: Int) {
        startActivity(DetailActivity.createIntent(this, productId))
    }

    private fun navigateToCart() {
        startActivity(CartActivity.createIntent(this))
    }
}
