package woowacourse.shopping.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.ui.cart.CartScreen
import woowacourse.shopping.ui.payment.PaymentScreen
import woowacourse.shopping.ui.productdetail.ProductDetailScreen
import woowacourse.shopping.ui.settings.SettingsScreen
import woowacourse.shopping.ui.shopping.ShoppingScreen

@Composable
fun AppNavHost(
    navigateToPaymentSignal: StateFlow<Boolean>,
    onPaymentNavigated: () -> Unit,
) {
    val navController = rememberNavController()
    val shouldNavigate by navigateToPaymentSignal.collectAsStateWithLifecycle()

    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            navController.navigate(CartRoute) {
                launchSingleTop = true
            }
            onPaymentNavigated()
        }
    }

    NavHost(
        navController = navController,
        startDestination = ShoppingRoute,
    ) {
        composable<ShoppingRoute> {
            ShoppingScreen(
                onCartClick = { navController.navigate(CartRoute) },
                onProductClick = { navController.navigate(ProductDetailRoute(it)) },
                onSettingsClick = { navController.navigate(SettingsRoute) },
            )
        }

        composable<ProductDetailRoute> {
            ProductDetailScreen(
                onCloseClick = {
                    navController.navigate(ShoppingRoute) {
                        popUpTo<ShoppingRoute> { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onAddToCartClick = {
                    navController.navigate(ShoppingRoute) {
                        popUpTo<ShoppingRoute> { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLastViewedProductClick = {
                    navController.navigate(
                        ProductDetailRoute(
                            id = it,
                            isFromBanner = true,
                        ),
                    )
                },
            )
        }

        composable<CartRoute> {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onOrderClick = { navController.navigate(PaymentRoute(it.toList())) },
            )
        }

        composable<PaymentRoute> {
            PaymentScreen(
                onBackClick = { navController.popBackStack() },
                onPayClick = {
                    navController.navigate(ShoppingRoute) {
                        popUpTo<ShoppingRoute> { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                modifier = Modifier,
            )
        }
    }
}
