package woowacourse.shopping.presentation.products

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.ActivityProductsBinding
import woowacourse.shopping.presentation.cart.CartActivity
import woowacourse.shopping.presentation.detail.ProductDetailActivity
import woowacourse.shopping.presentation.products.adapter.ProductsAdapter
import woowacourse.shopping.presentation.products.adapter.ProductsAdapterManager
import woowacourse.shopping.presentation.products.adapter.ProductsViewType

class ProductsActivity : AppCompatActivity() {
    private lateinit var shoppingApplication: ShoppingApplication
    private val binding: ActivityProductsBinding by lazy {
        ActivityProductsBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<ProductsViewModel> {
        shoppingApplication.getProductsViewModelFactory()
    }
    private val adapter by lazy {
        ProductsAdapter(
            actionHandler = viewModel,
        )
    }

    private val productDetailActivityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val changedProductId =
                    result.data?.getIntExtra(PRODUCT_ID_KEY, PRODUCT_ID_DEFAULT_VALUE)
                        ?: return@registerForActivityResult
                // viewModel.loadProduct(changedProductId)
                viewModel.loadRecentProducts()
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
        initializePage()
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
            Log.d("product", productsUiState.toString())
            adapter.updateProducts(productsUiState)
        }
    }

    private fun initializeProductAdapter() {
        binding.rvProducts.itemAnimator = null
        binding.rvProducts.adapter = adapter
        binding.rvProducts.layoutManager =
            ProductsAdapterManager(
                context = this,
                adapter = adapter,
                spanCount = 2,
                productViewType = ProductsViewType.Product.ordinal,
            )
        /*
        viewModel.recentProductUiModels.observe(this) {
            adapter.updateRecentProducts(it ?: return@observe)
        }
         */
    }

    private fun navigateToProductDetailView(productId: Int) {
        val intent = ProductDetailActivity.newIntent(this, productId)
        productDetailActivityResultLauncher.launch(intent)
    }

    private fun initializeToolbar() {
        binding.ivProductsCart.setOnClickListener { navigateToCartView() }
        binding.tvProductsCartCount.setOnClickListener { navigateToCartView() }
    }

    private fun navigateToCartView() {
        val intent = Intent(this, CartActivity::class.java)
        cartActivityResultLauncher.launch(intent)
    }

    private fun initializePage() {
        val onScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int,
                ) {
                    val lastPosition =
                        (recyclerView.layoutManager as GridLayoutManager).findLastCompletelyVisibleItemPosition()
                    val productsLastPosition = adapter.findProductsLastPosition(lastPosition)
                    // viewModel.changeSeeMoreVisibility(productsLastPosition)
                }
            }
        binding.rvProducts.addOnScrollListener(onScrollListener)
    }

    companion object {
        const val PRODUCT_ID_KEY = "changed_product_id_key"
        private const val PRODUCT_ID_DEFAULT_VALUE = -1
    }
}
