@file:Suppress("FunctionName")

package woowacourse.shopping.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.toRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import woowacourse.shopping.di.AppViewModelFactory
import woowacourse.shopping.ui.cart.ShoppingCartRouteContent
import woowacourse.shopping.ui.detail.DetailRouteContent
import woowacourse.shopping.ui.productlist.ProductListRouteContent
import woowacourse.shopping.ui.recommend.ShoppingCartRecommendRouteContent

@Composable
fun ShoppingNavHost(viewModelFactory: AppViewModelFactory) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ProductListRoute,
    ) {
        composable<ProductListRoute> {
            ProductListRouteContent(
                viewModelFactory = viewModelFactory,
                onNavigateToDetail = { productId ->
                    navController.navigate(
                        DetailRoute(
                            productId = productId,
                            showLastViewed = true,
                        ),
                    )
                },
                onNavigateToCart = {
                    navController.navigate(CartGraphRoute)
                },
            )
        }

        composable<DetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<DetailRoute>()
            DetailRouteContent(
                viewModelFactory = viewModelFactory,
                route = route,
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToLastViewed = { productId ->
                    navController.navigate(
                        DetailRoute(
                            productId = productId,
                            showLastViewed = true,
                        ),
                    )
                },
            )
        }

        navigation<CartGraphRoute>(startDestination = CartRoute) {
            composable<CartRoute> { backStackEntry ->
                val cartGraphEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(CartGraphRoute)
                    }

                ShoppingCartRouteContent(
                    viewModelFactory = viewModelFactory,
                    sharedViewModelStoreOwner = cartGraphEntry,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToRecommend = {
                        navController.navigate(RecommendRoute)
                    },
                )
            }

            composable<RecommendRoute> { backStackEntry ->
                val cartGraphEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(CartGraphRoute)
                    }

                ShoppingCartRecommendRouteContent(
                    viewModelFactory = viewModelFactory,
                    sharedViewModelStoreOwner = cartGraphEntry,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onOrderCompleted = {
                        navController.navigate(ProductListRoute) {
                            popUpTo(ProductListRoute) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}
