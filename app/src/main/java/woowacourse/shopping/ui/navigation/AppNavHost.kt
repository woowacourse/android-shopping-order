package woowacourse.shopping.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import woowacourse.shopping.notification.PaymentReminderReceiver
import woowacourse.shopping.ui.cart.CartScreen
import woowacourse.shopping.ui.payment.PaymentScreen
import woowacourse.shopping.ui.productdetail.ProductDetailScreen
import woowacourse.shopping.ui.settings.SettingsScreen
import woowacourse.shopping.ui.shopping.ShoppingScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = ShoppingRoute
    ) {
        composable<ShoppingRoute> {
            ShoppingScreen(
                onCartClick = { navController.navigate(CartRoute) },
                onProductClick = { navController.navigate(ProductDetailRoute(it)) },
                onSettingsClick = { navController.navigate(SettingsRoute) }
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
                            isFromBanner = true
                        )
                    )
                }
            )
        }

        composable<CartRoute> {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onOrderClick = { navController.navigate(PaymentRoute(it.toList())) }
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
                modifier = Modifier
            )
        }
    }
}
