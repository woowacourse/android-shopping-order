package woowacourse.shopping.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import woowacourse.shopping.ui.cart.CartScreenRoute
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.cart.payment.PaymentScreenRoute
import woowacourse.shopping.ui.cart.recommend.RecommendProductScreenRoute
import woowacourse.shopping.ui.detail.DetailScreenRoute
import woowacourse.shopping.ui.settings.SettingsScreenRoute
import woowacourse.shopping.ui.shopping.ShoppingScreenRoute

@Composable
fun ShoppingNavHost(
    modifier: Modifier = Modifier,
    paymentReminderNavigationCount: Int = 0,
) {
    val navController = rememberNavController()

    LaunchedEffect(paymentReminderNavigationCount) {
        if (paymentReminderNavigationCount <= 0) return@LaunchedEffect

        navController.navigate(CartGraph) {
            launchSingleTop = true
        }
        navController.navigate(Payment) {
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Shopping,
        modifier = modifier,
    ) {
        composable<Shopping> {
            ShoppingScreenRoute(
                onProductClick = { productId ->
                    navController.navigate(Detail(productId))
                },
                onCartClick = {
                    navController.navigate(CartGraph)
                },
                onSettingsClick = {
                    navController.navigate(Settings)
                },
            )
        }

        composable<Settings> {
            SettingsScreenRoute(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }

        composable<Detail> {
            DetailScreenRoute(
                onNavigateToShopping = {
                    navController.popBackStack(
                        route = Shopping,
                        inclusive = false,
                    )
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRecentItemClick = { recentProductId ->
                    navController.navigate(Detail(recentProductId)) {
                        popUpTo(Shopping)
                    }
                },
            )
        }

        navigation<CartGraph>(
            startDestination = Cart,
        ) {
            composable<Cart> { backStackEntry ->
                val cartGraphEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(CartGraph)
                    }

                CartScreenRoute(
                    cartViewModel = cartGraphEntry.cartViewModel(),
                    onBackClick = { navController.popBackStack() },
                    onOrderClick = { navController.navigate(Recommend) },
                )
            }

            composable<Recommend> { backStackEntry ->
                val cartGraphEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(CartGraph)
                    }

                RecommendProductScreenRoute(
                    cartViewModel = cartGraphEntry.cartViewModel(),
                    onBackClick = { navController.popBackStack() },
                    onOrderClick = { navController.navigate(Payment) },
                )
            }

            composable<Payment> { backStackEntry ->
                val cartGraphEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(CartGraph)
                    }

                PaymentScreenRoute(
                    cartViewModel = cartGraphEntry.cartViewModel(),
                    onBackClick = { navController.popBackStack() },
                    onPaymentClick = {
                        navController.navigate(Shopping) {
                            popUpTo(Shopping)
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun NavBackStackEntry.cartViewModel(): CartViewModel =
    viewModel(
        viewModelStoreOwner = this,
        factory = CartViewModel.Factory,
    )
