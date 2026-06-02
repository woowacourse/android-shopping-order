package woowacourse.shopping.ui.recommendation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import woowacourse.shopping.domain.model.order.PurchaseProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyViewedProductRepository
import woowacourse.shopping.ui.event.UiEventHandler
import woowacourse.shopping.ui.navigation.ShoppingRoute
import woowacourse.shopping.ui.uimodel.toCartProductUiModel
import woowacourse.shopping.ui.uimodel.toProductUiModel

fun NavGraphBuilder.recommendationRoute(
    cartRepository: CartRepository,
    productRepository: ProductRepository,
    recentlyViewedProductRepository: RecentlyViewedProductRepository,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onItemClick: (selectedProductId: Long) -> Unit,
    onOrderClick: (selectedCartItemIds: List<Long>) -> Unit,
    onBackClick: () -> Unit,
) {
    composable<ShoppingRoute.Recommendation> {
        RecommendationRouteContent(
            cartRepository = cartRepository,
            productRepository = productRepository,
            recentlyViewedProductRepository = recentlyViewedProductRepository,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
            onOrderClick = onOrderClick,
            onItemClick = onItemClick,
        )
    }
}

@Composable
private fun RecommendationRouteContent(
    cartRepository: CartRepository,
    productRepository: ProductRepository,
    recentlyViewedProductRepository: RecentlyViewedProductRepository,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onOrderClick: (selectedCartItemIds: List<Long>) -> Unit,
    onItemClick: (selectedProductId: Long) -> Unit,
) {
    val viewModel: RecommendationViewModel =
        viewModel(
            factory =
                RecommendationViewModelFactory(
                    cartRepository = cartRepository,
                    productRepository = productRepository,
                    recentlyViewedProductRepository = recentlyViewedProductRepository,
                ),
        )

    UiEventHandler(
        uiEvent = viewModel.uiEvent,
        snackbarHostState = snackbarHostState,
    )

    val selectedCartItemIds by viewModel.selectedItemIds.collectAsStateWithLifecycle()
    val totalPrice by viewModel.totalPrice.collectAsStateWithLifecycle()
    val totalCount by viewModel.selectedCount.collectAsStateWithLifecycle()
    val cartState by viewModel.allCartItems.collectAsStateWithLifecycle()
    val recommendedProducts by viewModel.recommendedProducts.collectAsStateWithLifecycle()
    val uiState =
        RecommendationUiState(
            recommendedProducts = recommendedProducts.toProductUiModel(),
            cartItems = cartState.toCartProductUiModel(),
            totalPrice = totalPrice,
            totalCount = totalCount,
        )

    CartRecommendationScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onOrderClick = { onOrderClick(selectedCartItemIds) },
        onAddInCart = { productId ->
            recommendedProducts.findWithId(productId)?.let { product ->
                viewModel.addToCart(PurchaseProduct(product.id, product))
            }
        },
        onAdd = { id, amount -> viewModel.updateCountWithID(id, amount) },
        onMinus = { id, amount -> viewModel.updateCountWithID(id, amount) },
        onDelete = { id -> viewModel.removeWithID(id) },
        onItemClick = onItemClick,
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding),
    )
}
