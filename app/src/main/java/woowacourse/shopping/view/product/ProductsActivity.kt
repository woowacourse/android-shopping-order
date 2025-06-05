package woowacourse.shopping.view.product

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityProductsBinding
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.common.GridItemDecoration
import woowacourse.shopping.view.common.QuantityObservable
import woowacourse.shopping.view.common.ResultFrom
import woowacourse.shopping.view.common.getSerializableExtraData
import woowacourse.shopping.view.common.showSnackBar
import woowacourse.shopping.view.product.viewModel.ProductsViewModel
import woowacourse.shopping.view.product.viewModel.RecentProductsViewModel
import woowacourse.shopping.view.productDetail.ProductDetailActivity
import woowacourse.shopping.view.shoppingCart.ShoppingCartActivity

class ProductsActivity :
    AppCompatActivity(),
    ProductAdapter.ProductListener {
    private val binding: ActivityProductsBinding by lazy {
        ActivityProductsBinding.inflate(layoutInflater)
    }

    private val viewModel: ProductsViewModel by viewModels()
    private val recentProductsViewModel: RecentProductsViewModel by viewModels()
    private val productAdapter: ProductAdapter by lazy { ProductAdapter(this) }
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            when (result.resultCode) {
                ResultFrom.PRODUCT_DETAIL_BACK.RESULT_OK -> {
                    val updateItem: Product? =
                        result.data?.getSerializableExtraData("updateProduct")
                    if (updateItem != null) {
                        viewModel.reload()
                    }
                    recentProductsViewModel.updateRecentProducts()
                }

                ResultFrom.SHOPPING_CART_BACK.RESULT_OK -> {
                    val updateItems: Boolean? =
                        result.data?.getSerializableExtraData("updateProducts")
                    if (updateItems != null) viewModel.reload()
                }

                ResultFrom.PRODUCT_RECENT_WATCHING_CLICK.RESULT_OK -> {
                    val recentProduct: Product =
                        result.data?.getSerializableExtraData("recentProduct")
                            ?: return@registerForActivityResult
                    navigateToRecentProduct(recentProduct)
                }
            }
        }

    private fun navigateToRecentProduct(product: Product) {
        activityResultLauncher.launch(ProductDetailActivity.newIntent(this, product.id))
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

        initDataBinding()
        setupObservers()
        setupAdapter()
    }

    private fun initDataBinding() {
        binding.adapter = productAdapter
        binding.onClickShoppingCartButton = ::navigateToShoppingCart
        binding.lifecycleOwner = this
        binding.viewModel = this@ProductsActivity.viewModel
    }

    private fun setupObservers() {
        viewModel.productsUi.observe(this) { products ->
            productAdapter.submitList(products)
        }

        recentProductsViewModel.recentProducts.observe(this) { recentProducts ->
            productAdapter.submitList(listOf(recentProducts))
        }

        viewModel.shoppingCartQuantity.observe(this) { shoppingCartQuantity: Int ->
            handleShoppingCartQuantity(shoppingCartQuantity)
        }

        viewModel.event.observe(this) { event: ProductsEvent ->
            handleUiEvent(event)
        }
    }

    private fun handleShoppingCartQuantity(shoppingCartQuantity: Int) {
        binding.productsShoppingCartQuantity.apply {
            visibility =
                if (shoppingCartQuantity > 0) View.VISIBLE else View.GONE
            text = shoppingCartQuantity.toString()
        }
    }

    private fun handleUiEvent(event: ProductsEvent) {
        when (event) {
            ProductsEvent.UPDATE_PRODUCT_FAILURE -> binding.root.showSnackBar(getString(R.string.products_update_products_error_message))
            ProductsEvent.NOT_ADD_TO_SHOPPING_CART ->
                binding.root.showSnackBar(
                    getString(R.string.product_detail_add_shopping_cart_error_message),
                )

            ProductsEvent.NOT_MINUS_TO_SHOPPING_CART ->
                binding.root.showSnackBar(
                    getString(R.string.products_minus_shopping_cart_product_error_message),
                )

            ProductsEvent.UPDATE_RECENT_WATCHING_PRODUCTS_FAILURE ->
                binding.root.showSnackBar(
                    getString(R.string.products_add_recent_watching_product_error_message),
                )
        }
    }

    private fun setupAdapter() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        gridLayoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                    when (ProductsItem.ItemType.from(productAdapter.getItemViewType(position))) {
                        ProductsItem.ItemType.PRODUCT -> 1
                        ProductsItem.ItemType.MORE -> 2
                        ProductsItem.ItemType.RECENT_WATCHING -> 2
                    }
            }

        binding.products.apply {
            layoutManager = gridLayoutManager
            addItemDecoration(
                GridItemDecoration(
                    context = this@ProductsActivity,
                    viewTypeToDecorate = ProductsItem.ItemType.PRODUCT.ordinal,
                    spanCount = 2,
                    sideMarginDp = 20,
                    itemSpacingDp = 12,
                ),
            )
        }
    }

    private fun navigateToShoppingCart() {
        activityResultLauncher.launch(ShoppingCartActivity.newIntent(this))
    }

    override fun onProductClick(productItem: ProductsItem.ProductItem) {
        activityResultLauncher.launch(
            ProductDetailActivity.newIntent(
                context = this,
                productId = productItem.product.id,
                shoppingCartId = productItem.shoppingCartId,
                quantity = productItem.selectedQuantity,
            ),
        )
    }

    override fun onLoadClick() {
        viewModel.updateMoreProducts()
    }

    override fun onRecentProductClick(productItem: ProductsItem.ProductItem) {
        activityResultLauncher.launch(
            ProductDetailActivity.newIntent(
                context = this,
                productId = productItem.product.id,
                shoppingCartId = productItem.shoppingCartId,
                quantity = productItem.selectedQuantity,
            ),
        )
    }

    override fun onPlusShoppingCartClick(quantityObservable: QuantityObservable) {
        val item = quantityObservable as ProductsItem.ProductItem
        viewModel.addProductToShoppingCart(item, item.selectedQuantity)
    }

    override fun onMinusShoppingCartClick(quantityObservable: QuantityObservable) {
        val item = quantityObservable as ProductsItem.ProductItem
        viewModel.minusProductToShoppingCart(item, item.selectedQuantity)
    }
}
