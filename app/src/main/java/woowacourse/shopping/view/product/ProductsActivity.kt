package woowacourse.shopping.view.product

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.product.source.LocalProductsDataSource
import woowacourse.shopping.data.product.source.LocalRecentViewedProductsDataSource
import woowacourse.shopping.databinding.ActivityProductsBinding
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.productDetail.ProductDetailActivity
import woowacourse.shopping.view.shoppingCart.ShoppingCartActivity
import woowacourse.shopping.view.showToast

class ProductsActivity : AppCompatActivity() {
    private val binding: ActivityProductsBinding by lazy {
        ActivityProductsBinding.inflate(layoutInflater)
    }
    private val viewModel: ProductsViewModel by viewModels()
    private val productsAdapter: ProductsAdapter by lazy {
        ProductsAdapter(::navigateToProductDetail, viewModel::loadMoreProducts)
    }

    private val detailActivityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.updateRecentViewedProducts()
            }
        }

    private val shoppingCartActivityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                viewModel.loadMoreProducts()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.productsRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        LocalProductsDataSource.init(applicationContext)
//        LocalShoppingCartDataSource.init(applicationContext)
        LocalRecentViewedProductsDataSource.init(applicationContext)

        initDataBinding()
        handleEventsFromViewModel()
//        bindData()

        binding.products.layoutManager =
            GridLayoutManager(this, spanCount).apply {
                spanSizeLookup =
                    object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int =
                            when (
                                ProductsItem.ItemType.from(
                                    productsAdapter.getItemViewType(
                                        position,
                                    ),
                                )
                            ) {
                                ProductsItem.ItemType.RECENT_VIEWED_PRODUCT -> spanCount
                                ProductsItem.ItemType.PRODUCT -> 1
                                ProductsItem.ItemType.MORE -> spanCount
                            }
                    }
            }
    }

    private val spanCount: Int
        get() {
            return if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                4
            } else {
                2
            }
        }

    private fun initDataBinding() {
        binding.adapter = productsAdapter
        binding.onClickShoppingCartButton = ::navigateToShoppingCart
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun handleEventsFromViewModel() {
        viewModel.event.observe(this) { event: ProductsEvent ->
            when (event) {
                ProductsEvent.UPDATE_PRODUCT_FAILURE ->
                    showToast(R.string.products_update_products_error_message)

                ProductsEvent.UPDATE_SHOPPING_CART_FAILURE ->
                    showToast(R.string.products_update_shopping_cart_error_message)

                ProductsEvent.LOAD_SHOPPING_CART_QUANTITY_FAILURE ->
                    showToast(R.string.products_load_shopping_cart_quantity_error_message)

                ProductsEvent.LOAD_RECENT_PRODUCTS_FAILURE ->
                    showToast(R.string.products_load_recent_products_error_message)
            }
        }
    }

//    private fun bindData() {
//        viewModel.productItems.observe(this) { productsItems: List<ProductsItem> ->
//            productsAdapter.submitList(productsItems)
//        }
//    }

    private fun navigateToShoppingCart() {
        shoppingCartActivityResultLauncher.launch(ShoppingCartActivity.newIntent(this))
    }

    private fun navigateToProductDetail(product: Product) {
        detailActivityResultLauncher.launch(
            ProductDetailActivity.newIntent(
                this,
                product.id,
                viewModel.getCartItemId(product),
            ),
        )
    }
}
