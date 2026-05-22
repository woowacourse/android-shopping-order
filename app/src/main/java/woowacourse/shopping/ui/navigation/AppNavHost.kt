package woowacourse.shopping.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import woowacourse.shopping.ui.cart.CartRecommendationRouteScreen
import woowacourse.shopping.ui.cart.CartRouteScreen
import woowacourse.shopping.ui.cart.list.CartViewModel
import woowacourse.shopping.ui.cart.recommendation.CartRecommendationViewModel
import woowacourse.shopping.ui.order.OrderScreen
import woowacourse.shopping.ui.productdetail.ProductDetailRouteScreen
import woowacourse.shopping.ui.shopping.ShoppingRouteScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = ShoppingRoute,
    ) {
        composable<ShoppingRoute> {
            ShoppingRouteScreen(
                onCartClick = { navController.navigate(CartGraph) },
                onProductClick = { productId ->
                    navController.navigate(ProductDetailRoute(productId))
                },
            )
        }

        composable<ProductDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ProductDetailRoute>()

            ProductDetailRouteScreen(
                productId = route.productId,
                onCloseClick = { navController.popBackStack() },
                onLastViewedProductClick = { productId ->
                    navController.navigate(ProductDetailRoute(productId))
                },
            )
        }

        navigation<CartGraph>(startDestination = CartRoute) {
            composable<CartRoute> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(CartGraph)
                }
                val cartViewModel: CartViewModel = viewModel(parentEntry)

                CartRouteScreen(
                    cartViewModel = cartViewModel,
                    onBackClick = { navController.popBackStack() },
                    onOrderClick = {
                        navController.navigate(OrderRoute)
                    },
                )
            }

            composable<OrderRoute> {
                OrderScreen(
                    onBackClick = { navController.popBackStack() },
                )
            }

            composable<CartRecommendationRoute> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(CartGraph)
                }
                val cartViewModel: CartViewModel = viewModel(parentEntry)
                val recommendationViewModel: CartRecommendationViewModel = viewModel(parentEntry)

                CartRecommendationRouteScreen(
                    cartViewModel = cartViewModel,
                    recommendationViewModel = recommendationViewModel,
                    onBackToCart = { navController.popBackStack() },
                    onProductClick = { productId ->
                        navController.navigate(ProductDetailRoute(productId))
                    },
                    onOrderCompleted = {
                        navController.navigate(ShoppingRoute) {
                            popUpTo(CartGraph) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}
