package woowacourse.shopping.ui.recommendation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.event.UiEventHandler
import woowacourse.shopping.ui.navigation.ShoppingRoute

fun NavGraphBuilder.recommendationRoute(
    shoppingApplication: ShoppingApplication,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onItemClick: (selectedProductId: Long) -> Unit,
    onOrderClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    composable<ShoppingRoute.Recommendation> { backStackEntry ->
        val route = backStackEntry.toRoute<ShoppingRoute.Recommendation>()

        RecommendationRouteContent(
            shoppingApplication = shoppingApplication,
            selectedCartItemIds = route.selectedCartItemIds,
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
    shoppingApplication: ShoppingApplication,
    selectedCartItemIds: List<Long>,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onOrderClick: () -> Unit,
    onItemClick: (selectedProductId: Long) -> Unit,
) {
    val viewModel: RecommendationViewModel =
        viewModel(
            factory =
                RecommendationViewModelFactory(
                    cartRepository = shoppingApplication.cartRepository,
                    productRepository = shoppingApplication.productRepository,
                    recentlyViewedProductRepository =
                        shoppingApplication.recentlyViewedProductRepository,
                    initialSelectedIds = selectedCartItemIds,
                ),
        )

    UiEventHandler(
        uiEvent = viewModel.uiEvent,
        snackbarHostState = snackbarHostState,
    )

    val totalPrice by viewModel.totalPrice.collectAsStateWithLifecycle()
    val totalCount by viewModel.selectedCount.collectAsStateWithLifecycle()
    val cartState by viewModel.allCartItems.collectAsStateWithLifecycle()
    val recommendedProducts by viewModel.recommendedProducts.collectAsStateWithLifecycle()

    CartRecommendationScreen(
        recommendedProducts = recommendedProducts,
        totalPrice = totalPrice,
        totalCount = totalCount,
        onBackClick = onBackClick,
        onOrderClick = onOrderClick,
        onAddInCart = { viewModel.addToCart(it) },
        onAdd = { id, amout -> viewModel.updateCountWithID(id, amout) },
        onMinus = { id, amount -> viewModel.updateCountWithID(id, amount) },
        onDelete = { id -> viewModel.removeWithID(id) },
        onItemClick = onItemClick,
        isContainedInCart = { id -> cartState.isContain(id) },
        itemCount = { id -> cartState.totalCountOfSpecificPurchaseProduct(id) },
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding),
    )
}
