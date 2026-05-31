package woowacourse.shopping.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import woowacourse.shopping.ui.cart.CartScreen
import woowacourse.shopping.ui.payment.PaymentScreen
import woowacourse.shopping.ui.productdetail.ProductDetailScreen
import woowacourse.shopping.ui.settings.SettingsScreen
import woowacourse.shopping.ui.shopping.ShoppingScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

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

        composable<PaymentRoute>(
            deepLinks =
                listOf(
                    navDeepLink<PaymentRoute>(
                        basePath = PaymentRoute.DEEP_LINK,
                    ),
                ),
        ) {
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
