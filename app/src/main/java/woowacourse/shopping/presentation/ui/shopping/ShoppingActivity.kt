package woowacourse.shopping.presentation.ui.shopping

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCartRepositoryImpl
import woowacourse.shopping.data.repository.RemoteShoppingRepositoryImpl
import woowacourse.shopping.databinding.ActivityShoppingBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.cart.CartActivity
import woowacourse.shopping.presentation.ui.detail.DetailActivity
import woowacourse.shopping.presentation.ui.shopping.adapter.RecentProductAdapter
import woowacourse.shopping.presentation.ui.shopping.adapter.ShoppingAdapter

class ShoppingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShoppingBinding
    private lateinit var shoppingAdapter: ShoppingAdapter
    private lateinit var recentProductAdapter: RecentProductAdapter

    private val viewModel: ShoppingViewModel by viewModels {
        ShoppingViewModelFactory(
            shoppingItemsRepository = RemoteShoppingRepositoryImpl(),
            cartItemsRepository = RemoteCartRepositoryImpl(),
            recentProductRepository = RecentProductRepositoryImpl(context = this),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpRecyclerView()

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        observeViewModel()
        showShimmerListTest()
    }

    override fun onResume() {
        super.onResume()
        viewModel.reloadProducts()
    }

    private fun setUpRecyclerView() {
        binding.rvProductList.layoutManager = GridLayoutManager(this, 2)
        setUpRecyclerViewAdapter()
        viewModel.hideLoadMore()
        checkLoadMoreBtnVisibility()
    }

    private fun setUpRecyclerViewAdapter() {
        shoppingAdapter = ShoppingAdapter(viewModel, viewModel)
        recentProductAdapter = RecentProductAdapter(viewModel)

        binding.rvProductList.adapter = shoppingAdapter
        binding.horizontalView.rvRecentProduct.adapter = recentProductAdapter

        try {
            viewModel.products.observe(this) { products ->
                shoppingAdapter.loadData(products)
            }
        } catch (exception: Exception) {
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.shoppingUiState.observe(this) { state ->
            when (state) {
                is UIState.Success -> showData(state.data)
                is UIState.Empty -> showData(emptyList())
                is UIState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
            }
        }

        viewModel.navigateToDetail.observe(this) {
            it.getContentIfNotHandled()?.let { productId ->
                navigateToDetail(productId)
            }
        }

        viewModel.navigateToCart.observe(this) {
            it.getContentIfNotHandled()?.let {
                navigateToCart()
            }
        }

        viewModel.shoppingProducts.observe(this) {
            shoppingAdapter.submitList(it)
        }

        viewModel.recentProducts.observe(this) { recentProducts ->
            recentProductAdapter.submitList(recentProducts) {
                binding.horizontalView.rvRecentProduct.scrollToPosition(0)
            }
        }
    }

    // TODO: 데이터 로딩 중인 경우에 LoadMore 버튼 숨기기
    private fun checkLoadMoreBtnVisibility() {
        binding.nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->
                if (!v.canScrollVertically(1)) {
                    viewModel.showLoadMoreByCondition()
                } else {
                    viewModel.hideLoadMore()
                }
            },
        )
    }

    private fun showData(data: List<Product>) {
        shoppingAdapter.loadData(data)
        shoppingAdapter.loadShoppingProductData(
            data.map {
                ShoppingProduct(
                    product = it,
                    quantity = viewModel.fetchQuantity(it.id),
                )
            },
        )
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun navigateToCart() {
        startActivity(CartActivity.createIntent(context = this))
    }

    private fun navigateToDetail(productId: Long) {
        startActivity(DetailActivity.createIntent(this, productId))
    }

    private fun showShimmerListTest() {
        lifecycleScope.launch {
            showProductData(isLoading = true)
            delay(2500)
            showProductData(isLoading = false)
        }
    }

    private fun showProductData(isLoading: Boolean) {
        if (isLoading) {
            binding.horizontalView.shimmerRecentProductList.startShimmer()
            binding.shimmerProductList.startShimmer()
            binding.horizontalView.shimmerRecentProductList.visibility = View.VISIBLE
            binding.shimmerProductList.visibility = View.VISIBLE
            binding.horizontalView.rvRecentProduct.visibility = View.GONE
            binding.rvProductList.visibility = View.GONE
        } else {
            binding.horizontalView.shimmerRecentProductList.stopShimmer()
            binding.shimmerProductList.stopShimmer()
            binding.horizontalView.shimmerRecentProductList.visibility = View.GONE
            binding.shimmerProductList.visibility = View.GONE
            binding.horizontalView.rvRecentProduct.visibility = View.VISIBLE
            binding.rvProductList.visibility = View.VISIBLE
        }
    }
}
