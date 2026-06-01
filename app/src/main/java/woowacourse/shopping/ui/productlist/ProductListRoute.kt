@file:Suppress("FunctionName")

package woowacourse.shopping.ui.productlist

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.di.AppViewModelFactory
import woowacourse.shopping.ui.cart.ShoppingCartViewModel
import woowacourse.shopping.ui.component.MoreButton

@Composable
fun ProductListRouteContent(
    viewModelFactory: AppViewModelFactory,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCart: () -> Unit,
) {
    val productListViewModel: ProductListViewModel = viewModel(factory = viewModelFactory)
    val shoppingCartViewModel: ShoppingCartViewModel = viewModel(factory = viewModelFactory)

    val uiState by productListViewModel.uiState.collectAsStateWithLifecycle()
    val hasApiError = uiState.errorMessage != null
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
    val recentViewedListState = rememberLazyListState()
    val latestRecentViewedProductId = visibleRecentViewedItems.firstOrNull()?.getProductId()

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        val shouldLoadInitialProducts =
            !productListViewModel.uiState.value.hasLoadedProducts &&
                !productListViewModel.uiState.value.isLoading
        if (shouldLoadInitialProducts) {
            productListViewModel.requestProduct(size = MAX_PRODUCT_SIZE)
        }
        shoppingCartViewModel.requestCartItems()
    }

    LaunchedEffect(productListViewModel) {
        productListViewModel.event.collect { event ->
            when (event) {
                is ProductListViewModel.ProductListEvent.NavigateToDetailProduct -> {
                    onNavigateToDetail(event.productId)
                }

                ProductListViewModel.ProductListEvent.NavigateToShoppingCart -> {
                    onNavigateToCart()
                }
            }
        }
    }

    LaunchedEffect(latestRecentViewedProductId) {
        if (latestRecentViewedProductId != null) {
            recentViewedListState.animateScrollToItem(index = 0)
        }
    }

    ProductListScreen(
        shoppingItems = visibleShoppingItems,
        recentViewedShoppingItems = visibleRecentViewedItems,
        recentViewedListState = recentViewedListState,
        shoppingCartTotalCount = if (hasApiError) 0 else uiState.shoppingCartTotalCount,
        isLoading = uiState.isLoading,
        onAddToCartClick = { shoppingItem ->
            shoppingCartViewModel.addOrIncreaseByProductId(
                productId = shoppingItem.getProductId(),
                amount = 1,
            )
        },
        onQuantityPlusClick = { shoppingItem ->
            shoppingCartViewModel.addOrIncreaseByProductId(
                productId = shoppingItem.getProductId(),
                amount = 1,
            )
        },
        onQuantityMinusClick = { shoppingItem ->
            shoppingCartViewModel.decreaseByProductId(shoppingItem.getProductId())
        },
        onProductClick = productListViewModel::onProductClick,
        onRecentViewedProductClick = productListViewModel::onProductClick,
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

private const val MAX_PRODUCT_SIZE: Int = 20
