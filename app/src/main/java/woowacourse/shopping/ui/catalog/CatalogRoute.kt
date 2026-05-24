package woowacourse.shopping.ui.catalog

import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.domain.model.order.PurchaseProduct
import woowacourse.shopping.ui.event.UiEventHandler
import woowacourse.shopping.ui.uimodel.toCartProductUiModel
import woowacourse.shopping.ui.uimodel.toProductUiModel
import woowacourse.shopping.ui.navigation.ShoppingRoute

fun NavGraphBuilder.catalogRoute(
    shoppingApplication: ShoppingApplication,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onProductClick: (selectedProductId: Long, lastViewedProductId: Long?) -> Unit,
    onCartClick: () -> Unit,
) {
    composable<ShoppingRoute.Catalog> {
        CatalogRouteContent(
            shoppingApplication = shoppingApplication,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onProductClick = onProductClick,
            onCartClick = onCartClick,
        )
    }
}

@Composable
private fun CatalogRouteContent(
    shoppingApplication: ShoppingApplication,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onProductClick: (selectedProductId: Long, lastViewedProductId: Long?) -> Unit,
    onCartClick: () -> Unit,
) {
    val viewModel =
        viewModel<ShoppingViewModel>(
            factory =
                ShoppingViewModelFactory(
                    shoppingApplication.cartRepository,
                    shoppingApplication.recentlyViewedProductRepository,
                    shoppingApplication.productRepository,
                ),
        )

    UiEventHandler(
        uiEvent = viewModel.uiEvent,
        snackbarHostState = snackbarHostState,
    )

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.fetchCart()
    }

    val cartState by viewModel.cart.collectAsStateWithLifecycle()
    val viewHistory by viewModel.recentlyViewedProducts.collectAsStateWithLifecycle()
    val currentProducts by viewModel.products.collectAsStateWithLifecycle()
    val lastViewedProductId by viewModel.lastViewProductId.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val cartProductCount by viewModel.cartProductCount.collectAsStateWithLifecycle()
    val uiState =
        CatalogUiState(
            products = currentProducts.toProductUiModel(),
            recentlyViewedProducts = viewHistory.toProductUiModel(),
            cartItems = cartState.toCartProductUiModel(),
            totalCount = cartProductCount,
            isLoading = isLoading,
        )

    CatalogScreen(
        uiState = uiState,
        onRecentlyViewedClick = { productId ->
            currentProducts.findWithId(productId)?.let { viewModel.updateHistory(it) }
            onProductClick(productId, lastViewedProductId)
        },
        onItemClick = { productId ->
            currentProducts.findWithId(productId)?.let { viewModel.updateHistory(it) }
            onProductClick(productId, lastViewedProductId)
        },
        onCartClick = onCartClick,
        onLoadClick = {
            viewModel.loadMore()
        },
        modifier = Modifier.padding(contentPadding),
        onAdd = { id, updateAmount ->
            viewModel.updateCountWithID(id, updateAmount)
        },
        onMinus = { id, updateAmount ->
            viewModel.updateCountWithID(id, updateAmount)
        },
        onDelete = { viewModel.removeWithID(it) },
        onAddInCart = { productId ->
            currentProducts.findWithId(productId)?.let { product ->
                viewModel.addToCart(PurchaseProduct(product.id, product))
            }
        },
    )
}
