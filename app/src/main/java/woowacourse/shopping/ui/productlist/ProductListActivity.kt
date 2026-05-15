@file:Suppress("FunctionName")

package woowacourse.shopping.ui.productlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.di.ApiViewModelFactory
import woowacourse.shopping.di.ScreenViewModelFactory
import woowacourse.shopping.ui.cart.ShoppingCartActivity
import woowacourse.shopping.ui.cart.ShoppingCartViewModel
import woowacourse.shopping.ui.component.MoreButton
import woowacourse.shopping.ui.detail.DetailProductActivity
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

class ProductListActivity : ComponentActivity() {
    private val app: ShoppingApplication by lazy { application as ShoppingApplication }

    private val screenViewModelFactory: ScreenViewModelFactory by lazy {
        ScreenViewModelFactory(appContainer = app.appContainer)
    }
    private val apiViewModelFactory: ApiViewModelFactory by lazy {
        ApiViewModelFactory(app.retrofitService)
    }

    private val productListViewModel: ProductListViewModel by viewModels { screenViewModelFactory }
    private val productViewModel: ProductViewModel by viewModels { apiViewModelFactory }
    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels { apiViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        observeApiViewModels()
        observeScreenEvents()
        requestProductsAndCart()

        setContent {
            val uiState = productListViewModel.uiState.collectAsStateWithLifecycle()
            val state = productViewModel.state.collectAsStateWithLifecycle()
            val hasApiError = state.value.errorMessage != null
            val visibleShoppingItems =
                if (hasApiError) {
                    emptyList()
                } else {
                    uiState.value.shoppingItems
                }
            val visibleRecentViewedItems =
                if (hasApiError) {
                    emptyList()
                } else {
                    uiState.value.recentViewedShoppingItems
                }

            AndroidShoppingTheme {
                ProductListScreen(
                    shoppingItems = visibleShoppingItems,
                    recentViewedShoppingItems = visibleRecentViewedItems,
                    shoppingCartTotalCount = if (hasApiError) 0 else uiState.value.shoppingCartTotalCount,
                    isNetworkConnected = uiState.value.isNetworkConnected,
                    state = state.value,
                    onAddToCartClick = { shoppingItem ->
                        productListViewModel.addProductToCart(shoppingItem)
                        shoppingCartViewModel.addOrIncreaseByProductId(
                            productId = shoppingItem.getProductId(),
                            amount = 1,
                        )
                    },
                    onQuantityPlusClick = { shoppingItem ->
                        productListViewModel.increaseProductQuantity(shoppingItem)
                        shoppingCartViewModel.addOrIncreaseByProductId(
                            productId = shoppingItem.getProductId(),
                            amount = 1,
                        )
                    },
                    onQuantityMinusClick = { shoppingItem ->
                        productListViewModel.decreaseProductQuantity(shoppingItem)
                        shoppingCartViewModel.decreaseByProductId(shoppingItem.getProductId())
                    },
                    onProductClick = productListViewModel::onProductClick,
                    onRecentViewedProductClick = productListViewModel::onRecentViewedProductClick,
                    onNavigateToCartClick = productListViewModel::onNavigateToCartClick,
                    bottomContent =
                        if (uiState.value.canLoadNextPage) {
                            {
                                MoreButton(
                                    onClick = productListViewModel::loadNextPage,
                                )
                            }
                        } else {
                            null
                        },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestProductsAndCart()
    }

    private fun requestProductsAndCart() {
        productViewModel.requestProduct(size = MAX_PRODUCT_SIZE)
        shoppingCartViewModel.requestCartItems()
    }

    private fun observeApiViewModels() {
        lifecycleScope.launch {
            /**
             * repeatOnLifecycle은 어떻게 동작을 하나요? 🤔
             */
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    productViewModel.products.collect { products ->
                        app.appContainer.remoteShoppingStateSyncer.syncProducts(products)
                    }
                }
                launch {
                    shoppingCartViewModel.shoppingCartItems.collect { shoppingCartItems ->
                        app.appContainer.remoteShoppingStateSyncer.syncCartItems(shoppingCartItems)
                    }
                }
            }
        }
    }

    private fun observeScreenEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productListViewModel.event.collect { event ->
                    when (event) {
                        is ProductListViewModel.ProductListEvent.NavigateToDetailProduct -> {
                            DetailProductActivity.start(
                                context = this@ProductListActivity,
                                productId = event.productId,
                                showLastViewed = event.showLastViewed,
                            )
                        }

                        ProductListViewModel.ProductListEvent.NavigateToShoppingCart -> {
                            ShoppingCartActivity.start(this@ProductListActivity)
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private const val MAX_PRODUCT_SIZE = 100
    }
}
