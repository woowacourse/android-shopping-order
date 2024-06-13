package woowacourse.shopping.ui.products

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.remote.RemoteCartRepository
import woowacourse.shopping.data.local.ShoppingCartDataBase
import woowacourse.shopping.data.product.remote.RemoteProductRepository
import woowacourse.shopping.data.recent.local.RoomRecentProductRepository
import woowacourse.shopping.data.remote.ApiError
import woowacourse.shopping.databinding.ActivityProductsBinding
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.detail.ProductDetailActivity
import woowacourse.shopping.ui.products.adapter.ProductsAdapter
import woowacourse.shopping.ui.products.adapter.ProductsSpanSizeLookUp

class ProductsActivity : AppCompatActivity() {
    private val binding: ActivityProductsBinding by lazy {
        ActivityProductsBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<ProductsViewModel> {
        ProductsViewModelFactory(
            RemoteProductRepository,
            RoomRecentProductRepository.getInstance(
                ShoppingCartDataBase.getInstance(
                    applicationContext,
                ).recentProductDao(),
            ),
            RemoteCartRepository,
        )
    }
    private val adapter by lazy {
        ProductsAdapter(
            onClickProductItem = { navigateToProductDetailView(it) },
            onClickRecentProductItem = { navigateToProductDetailView(it) },
            onIncreaseProductQuantity = { viewModel.increaseQuantity(it) },
            onDecreaseProductQuantity = { viewModel.decreaseQuantity(it) },
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
                viewModel.loadProduct(changedProductId)
                viewModel.loadRecentProducts()
            }
        }

    private val cartActivityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.loadProducts()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initializeView()
    }

    private fun initializeView() {
        initializeProductList()
        initializeToolbar()
        initializePage()
        observeErrorEvent()
    }

    private fun initializeProductList() {
        binding.rvProducts.itemAnimator = null
        binding.rvProducts.layoutManager =
            GridLayoutManager(this, PRODUCT_LIST_SPAN_SIZE).apply {
                spanSizeLookup = ProductsSpanSizeLookUp(adapter)
            }
        binding.rvProducts.adapter = adapter
        viewModel.productUiModels.observe(this) {
            adapter.updateProducts(it)
        }
        viewModel.recentProductUiModels.observe(this) {
            adapter.updateRecentProducts(it ?: return@observe)
        }
    }

    private fun initializeToolbar() {
        binding.ivProductsCart.setOnClickListener { navigateToCartView() }
        binding.tvProductsCartCount.setOnClickListener { navigateToCartView() }
    }

    private fun navigateToCartView() {
        val intent = Intent(this, CartActivity::class.java)
        cartActivityResultLauncher.launch(intent)
    }

    private fun navigateToProductDetailView(productId: Int) {
        val intent = ProductDetailActivity.newIntent(this, productId)
        productDetailActivityResultLauncher.launch(intent)
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
                    viewModel.changeSeeMoreVisibility(productsLastPosition)
                }
            }
        binding.rvProducts.addOnScrollListener(onScrollListener)
    }

    private fun observeErrorEvent() {
        viewModel.productsLoadError.observe(this) {
            showProductsErrorToast(it, R.string.product_load_error)
        }
        viewModel.cartItemAddError.observe(this) {
            showProductsErrorToast(it, R.string.cart_item_add_error)
        }
        viewModel.cartItemDeleteError.observe(this) {
            showProductsErrorToast(it, R.string.cart_item_delete_error)
        }
    }

    private fun showProductsErrorToast(
        throwable: Throwable,
        @StringRes errorMessageResId: Int,
    ) {
        if (throwable is ApiError) {
            showToast(errorMessageResId)
        }
        when (throwable) {
            is ApiError.BadRequest -> showToast(errorMessageResId)
            is ApiError.Unauthorized -> showToast(R.string.unauthorized_error)
            is ApiError.Forbidden -> showToast(R.string.unauthorized_error)
            is ApiError.NotFound -> showToast(R.string.product_not_found_error)
            is ApiError.InternalServerError -> showToast(R.string.server_error)
            is ApiError.Exception -> showToast(errorMessageResId)
        }
    }

    private fun showToast(
        @StringRes messageResId: Int,
    ) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val PRODUCT_LIST_SPAN_SIZE = 2

        const val PRODUCT_ID_KEY = "changed_product_id_key"
        private const val PRODUCT_ID_DEFAULT_VALUE = -1
    }
}
