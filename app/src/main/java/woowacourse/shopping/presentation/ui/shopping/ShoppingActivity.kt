package woowacourse.shopping.presentation.ui.shopping

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCartRepositoryImpl
import woowacourse.shopping.data.repository.RemoteShoppingRepositoryImpl
import woowacourse.shopping.databinding.ActivityShoppingBinding
import woowacourse.shopping.presentation.ui.SharedChangedIdsDB
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
            recentProductRepository = RecentProductRepositoryImpl(context = applicationContext),
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
        binding.countHandler = viewModel
        observeViewModel()
        loadProductsInfo(savedInstanceState)
    }

    private fun setUpRecyclerView() {
        shoppingAdapter = ShoppingAdapter(viewModel, viewModel)
        recentProductAdapter = RecentProductAdapter(viewModel)

        binding.rvProductList.adapter = shoppingAdapter
        binding.horizontalView.rvRecentProduct.adapter = recentProductAdapter
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                showSkeletonUI(it)
            }
        }

        viewModel.shoppingProducts.observe(this) {
            shoppingAdapter.setShoppingProducts(it)
        }

        viewModel.changedIds.observe(this) {
            shoppingAdapter.updateShoppingProducts(it)
        }

        viewModel.recentProducts.observe(this) {
            recentProductAdapter.updateProducts(it)
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
    }

    private fun loadProductsInfo(savedInstanceState: Bundle?) {
        viewModel.setLoadingStart()
        if (savedInstanceState == null) {
            viewModel.loadProducts()
        } else {
            viewModel.setLoadingEnd()
        }
    }

    private fun navigateToCart() {
        startActivity(CartActivity.createIntent(context = this))
    }

    private fun navigateToDetail(productId: Long) {
        startActivity(DetailActivity.createIntent(this, productId))
    }

    private fun showSkeletonUI(isLoading: Boolean) {
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

    override fun onResume() {
        super.onResume()
        if (SharedChangedIdsDB.existChangedProducts()) {
            viewModel.acceptChangedItems(SharedChangedIdsDB.getChangedProductsIds())
            SharedChangedIdsDB.clearChangedProductsId()
        }
    }
}
