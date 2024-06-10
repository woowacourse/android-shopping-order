package woowacourse.shopping.presentation.products

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.common.observeEvent
import woowacourse.shopping.databinding.ActivityProductsBinding
import woowacourse.shopping.presentation.cart.CartActivity
import woowacourse.shopping.presentation.detail.ProductDetailActivity
import woowacourse.shopping.presentation.detail.ProductDetailActivity.Companion.PRODUCT_ID_KEY
import woowacourse.shopping.presentation.products.adapter.ProductsAdapter
import woowacourse.shopping.presentation.products.adapter.ProductsAdapterManager
import woowacourse.shopping.presentation.products.adapter.ProductsViewType
import woowacourse.shopping.presentation.products.adapter.RecentProductsAdapter

class ProductsActivity : AppCompatActivity() {
    private lateinit var shoppingApplication: ShoppingApplication
    private val binding: ActivityProductsBinding by lazy {
        ActivityProductsBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<ProductsViewModel> {
        shoppingApplication.getProductsViewModelFactory()
    }
    private val recentProductsAdapter by lazy {
        RecentProductsAdapter(
            actionHandler = viewModel,
        )
    }
    private val productsAdapter by lazy {
        ProductsAdapter(
            productCountActionHandler = viewModel,
            productsActionHandler = viewModel,
            recentProductsAdapter = recentProductsAdapter,
        )
    }

    private val detailActivityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val productsId = result.data?.getIntExtra(PRODUCT_ID_KEY, -1) ?: -1
                viewModel.updateQuantity(productsId)
            }
        }

    private val cartActivityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // viewModel.loadProducts()
            }
        }

    override fun onResume() {
        super.onResume()
        viewModel.loadPage()
        viewModel.loadRecentProducts()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shoppingApplication = application as ShoppingApplication
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initializeView()
    }

    private fun initializeView() {
        initializeProductAdapter()
        initializeToolbar()
        viewModel.productsUiState.observe(this) { productsUiState ->
            if (productsUiState.isLoading) {
                binding.layoutProductsSkeleton.visibility = View.VISIBLE
                binding.rvProducts.visibility = View.GONE
                return@observe
            }
            if (productsUiState.isError) {
                Toast.makeText(this, R.string.load_page_error, Toast.LENGTH_SHORT).show()
                return@observe
            }
            binding.layoutProductsSkeleton.visibility = View.GONE
            binding.rvProducts.visibility = View.VISIBLE
            productsAdapter.updateProducts(productsUiState)
        }
        viewModel.navigateAction.observeEvent(this) { navigateAction ->
            when (navigateAction) {
                is ProductsNavigateAction.ProductDetailNavigateAction -> {
                    val intent = ProductDetailActivity.getIntent(this, navigateAction.productId)
                    detailActivityResultLauncher.launch(intent)
                }

                is ProductsNavigateAction.CartNavigateAction ->
                    CartActivity.startActivity(this)
            }
        }
        viewModel.recentProductUiModels.observe(this) { recentProductUiModels ->
            recentProductsAdapter.submitList(recentProductUiModels)
        }
    }

    private fun initializeProductAdapter() {
        binding.rvProducts.itemAnimator = null
        binding.rvProducts.adapter = productsAdapter
        binding.rvProducts.layoutManager =
            ProductsAdapterManager(
                context = this,
                adapter = productsAdapter,
                spanCount = 2,
                productViewType = ProductsViewType.Product.ordinal,
            )
    }

    private fun initializeToolbar() {
        binding.ivProductsCart.setOnClickListener { navigateToCartView() }
        binding.tvProductsCartCount.setOnClickListener { navigateToCartView() }
    }

    private fun navigateToCartView() {
        val intent = Intent(this, CartActivity::class.java)
        cartActivityResultLauncher.launch(intent)
    }
}
