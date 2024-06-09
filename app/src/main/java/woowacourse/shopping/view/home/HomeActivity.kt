package woowacourse.shopping.view.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.DefaultShoppingApplication
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityHomeBinding
import woowacourse.shopping.view.cart.CartActivity
import woowacourse.shopping.view.detail.DetailActivity
import woowacourse.shopping.view.home.product.HomeViewItem.Companion.LOAD_MORE_BUTTON_VIEW_TYPE
import woowacourse.shopping.view.home.product.HomeViewItem.ProductViewItem
import woowacourse.shopping.view.home.product.ProductAdapter
import woowacourse.shopping.view.home.recent.RecentProductAdapter

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recentProductAdapter: RecentProductAdapter
    private val viewModel: HomeViewModel by viewModels {
        (application as DefaultShoppingApplication).homeViewModelFactory
    }
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            val changedIds = getChangedIdsFromActivityResult(result)
            viewModel.updateProductQuantities(changedIds = changedIds)
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
                is HomeUiEvent.Error -> showError(getString(R.string.unknown_error))
            }
        }
    }

    private fun showData(data: List<ProductViewItem>) {
        productAdapter.loadData(data)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun navigateToDetail(productId: Int) {
        activityResultLauncher.launch(DetailActivity.createIntent(this, productId))
    }

    private fun navigateToCart() {
        activityResultLauncher.launch(CartActivity.createIntent(this))
    }

    private fun getChangedIdsFromActivityResult(result: ActivityResult): IntArray =
        if (result.resultCode == RESULT_OK) {
            result.data?.getIntArrayExtra(EXTRA_CHANGED_IDS)
        } else {
            intArrayOf()
        } ?: intArrayOf()

    companion object {
        private const val EXTRA_CHANGED_IDS = "extra_changed_ids"

        fun createIntent(
            context: Context,
            changedIds: IntArray,
        ): Intent {
            return Intent(context, HomeActivity::class.java)
                .putExtra(EXTRA_CHANGED_IDS, changedIds)
        }
    }
}
