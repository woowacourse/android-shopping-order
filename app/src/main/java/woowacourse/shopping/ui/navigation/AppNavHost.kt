package woowacourse.shopping.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.ui.cart.CartScreenRoute
import woowacourse.shopping.ui.common.SettingsScreen
import woowacourse.shopping.ui.payment.PaymentScreenRoute
import woowacourse.shopping.ui.productDetail.ProductDetailScreenRoute
import woowacourse.shopping.ui.productList.ProductListScreenRoute

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    appContainer: AppContainer,
    showSnackbar: (String) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = ProductListRoute,
        modifier = modifier,
    ) {
        composable<ProductListRoute> {
            ProductListScreenRoute(
                appContainer = appContainer,
                showSnackbar = showSnackbar,
                onCartClick = { navController.navigate(CartRoute) },
                onSettingsClick = { navController.navigate(SettingsRoute) },
                onProductClick = { product ->
                    navController.navigate(ProductDetailRoute(productId = product.id))
                },
            )
        }

        composable<ProductDetailRoute> {
            ProductDetailScreenRoute(
                appContainer = appContainer,
                showSnackbar = showSnackbar,
                onCloseClick = { navController.popBackStack() },
                onNavigateToCart = { navController.navigate(CartRoute) },
                onLastViewedProductClick = { product ->
                    navController.navigate(ProductDetailRoute(productId = product.id))
                },
            )
        }

        composable<CartRoute> {
            CartScreenRoute(
                appContainer = appContainer,
                showSnackbar = showSnackbar,
                onClickClose = { navController.popBackStack() },
                onNavigateToPayment = { selectedIds ->
                    navController.navigate(PaymentRoute(selectedItemIds = selectedIds))
                },
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable<PaymentRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<PaymentRoute>()
            PaymentScreenRoute(
                selectedItemIds = route.selectedItemIds,
                appContainer = appContainer,
                showSnackbar = showSnackbar,
                onClose = { navController.popBackStack() },
                onOrderSucceeded = {
                    navController.navigate(ProductListRoute) {
                        popUpTo<ProductListRoute> { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
