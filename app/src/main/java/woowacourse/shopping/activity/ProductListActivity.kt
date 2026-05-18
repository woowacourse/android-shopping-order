@file:Suppress("FunctionName")

package woowacourse.shopping.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.backend.retrofit.viewmodel.ProductViewModel
import woowacourse.shopping.ui.component.MoreButton
import woowacourse.shopping.ui.screen.ProductListScreen
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import woowacourse.shopping.ui.viewmodel.ProductListViewModel
import woowacourse.shopping.ui.viewmodel.ScreenViewModelFactory

class ProductListActivity : ComponentActivity() {
    private val app: ShoppingApplication by lazy { application as ShoppingApplication }

    private val screenViewModelFactory: ScreenViewModelFactory by lazy {
        ScreenViewModelFactory(
            appContainer = app.appContainer,
            retrofitService = app.retrofitService
        )
    }

    private val productListViewModel: ProductListViewModel by viewModels { screenViewModelFactory }

    private val productViewModel: ProductViewModel by viewModels { screenViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        observeApiViewModels()
        observeScreenEvents()
        requestProductsAndCart()

        setContent {
            val uiState by productListViewModel.uiState.collectAsStateWithLifecycle()
            val apiState by productViewModel.state.collectAsStateWithLifecycle()

            val errorMessage = apiState.errorMessage
            val hasApiError = errorMessage != null
            val visibleShoppingItems =
                if (hasApiError) {
                    emptyList()
                } else {
                    uiState.shoppingItems
                }
            val visibleRecentViewedItems =
                if (hasApiError) {
                    emptyList()
                } else {
                    uiState.recentViewedShoppingItems
                }

            AndroidShoppingTheme {
                ProductListScreen(
                    shoppingItems = visibleShoppingItems,
                    recentViewedShoppingItems = visibleRecentViewedItems,
                    shoppingCartTotalCount = if (hasApiError) 0 else uiState.shoppingCartTotalCount,
                    isNetworkConnected = uiState.isNetworkConnected,
                    state = apiState,
                    onAddToCartClick = { shoppingItem ->
                        productListViewModel.addProductToCart(shoppingItem)
                    },
                    onQuantityPlusClick = { shoppingItem ->
                        productListViewModel.increaseProductQuantity(shoppingItem)
                    },
                    onQuantityMinusClick = { shoppingItem ->
                        productListViewModel.decreaseProductQuantity(shoppingItem)
                    },
                    onProductClick = productListViewModel::onProductClick,
                    onRecentViewedProductClick = productListViewModel::onRecentViewedProductClick,
                    onNavigateToCartClick = productListViewModel::onNavigateToCartClick,
                    bottomContent =
                        if (uiState.canLoadNextPage) {
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
    }

    private fun observeApiViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    productViewModel.products.collect { products ->
                        app.appContainer.remoteShoppingStateSyncer.syncProducts(products)
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
