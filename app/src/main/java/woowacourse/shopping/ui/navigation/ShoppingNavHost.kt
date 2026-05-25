package woowacourse.shopping.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.cart.cartRoute
import woowacourse.shopping.ui.catalog.catalogRoute
import woowacourse.shopping.ui.payment.paymentRoute
import woowacourse.shopping.ui.productdetail.productDetailRoute
import woowacourse.shopping.ui.recommendation.recommendationRoute

@Composable
fun ShoppingNavHost(
    navController: NavHostController,
    shoppingApplication: ShoppingApplication,
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = ShoppingRoute.Catalog,
        modifier = modifier,
    ) {
        catalogRoute(
            shoppingApplication = shoppingApplication,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onProductClick = { selectedProductId, lastViewedProductId ->
                navController.navigate(
                    ShoppingRoute.ProductDetail(
                        selectedProductId = selectedProductId,
                        lastViewedProductId = lastViewedProductId,
                    ),
                )
            },
            onCartClick = {
                navController.navigate(
                    ShoppingRoute.Cart,
                )
            },
        )

        productDetailRoute(
            shoppingApplication = shoppingApplication,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onLastViewedProductClick = { selectedProductId, lastViewedProductId ->
                navController.navigate(
                    ShoppingRoute.ProductDetail(
                        selectedProductId = selectedProductId,
                        lastViewedProductId = lastViewedProductId,
                    ),
                )
            },
            onBackClick = {
                navController.popBackStack()
            },
        )

        cartRoute(
            shoppingApplication = shoppingApplication,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onBackClick = { navController.popBackStack() },
            onOrderClick = { selectedCartItemIds ->
                navController.navigate(
                    ShoppingRoute.Recommendation(
                        selectedCartItemIds = selectedCartItemIds,
                    ),
                )
            },
        )

        recommendationRoute(
            shoppingApplication = shoppingApplication,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onItemClick = { selectedProductId ->
                navController.navigate(
                    ShoppingRoute.ProductDetail(
                        selectedProductId = selectedProductId,
                        lastViewedProductId = null,
                    ),
                )
            },
            onOrderClick = { selectedCartItemIds ->
                navController.navigate(
                    ShoppingRoute.Payment(
                        selectedCartItemIds = selectedCartItemIds,
                    ),
                )
            },
            onBackClick = { navController.popBackStack() },
        )

        paymentRoute(
            shoppingApplication = shoppingApplication,
            contentPadding = contentPadding,
            snackbarHostState = snackbarHostState,
            onBackClick = { navController.popBackStack() },
            onPaymentComplete = {
                navController.navigate(ShoppingRoute.Catalog) {
                    popUpTo(ShoppingRoute.Catalog) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
        )
    }
}
