package woowacourse.shopping

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import woowacourse.shopping.feature.cart.component.CartScreen
import woowacourse.shopping.feature.productdetail.component.ProductDetailScreen
import woowacourse.shopping.feature.productlist.component.ProductListScreen
import woowacourse.shopping.feature.purchase.PurchaseScreen
import woowacourse.shopping.feature.recommend.component.RecommendScreen
import woowacourse.shopping.feature.setting.component.SettingScreen

@Composable
fun AppNavHost(startDestination: Any = ProductListRoute) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<ProductListRoute> {
            ProductListScreen(
                onProductClick = { productId, recentProductId ->
                    navController.navigate(ProductDetailRoute(productId, recentProductId))
                },
                onCartIconClick = {
                    navController.navigate(CartRoute)
                },
                onSettingIconClick = {
                    navController.navigate(SettingRoute)
                },
                activityFinish = {
                    navController.popBackStack()
                },
            )
        }

        composable<ProductDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ProductDetailRoute>()

            ProductDetailScreen(
                id = route.productId,
                recentProductId = route.recentProductId,
                activityFinish = { navController.popBackStack() },
                onClickRecentButton = {
                    if (route.recentProductId != null) {
                        navController.navigate(
                            ProductDetailRoute(
                                productId = route.recentProductId,
                                recentProductId = null,
                            ),
                        ) {
                            popUpTo<ProductListRoute> {
                                inclusive = false
                            }
                        }
                    }
                },
            )
        }

        composable<CartRoute> {
            CartScreen(
                activityFinish = { navController.popBackStack() },
                onToRecommendIntent = { cartContentIds ->
                    navController.navigate(RecommendRoute(contentIds = cartContentIds))
                },
            )
        }

        composable<RecommendRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<RecommendRoute>()
            RecommendScreen(
                onCloseClick = { navController.popBackStack() },
                onOrderClick = { contentIds, totalPrice ->
                    navController.navigate(
                        PurchaseRoute(
                            contentIds = contentIds,
                            totalPrice = totalPrice,
                        ),
                    ) {
                        popUpTo<RecommendRoute> {
                            inclusive = true
                        }
                    }
                },
                contentIds = route.contentIds,
            )
        }

        composable<PurchaseRoute> {
            PurchaseScreen(
                activityFinish = { navController.popBackStack() },
                onPurchaseComplete = {
                    navController.popBackStack(ProductListRoute, inclusive = false)
                },
            )
        }

        composable<SettingRoute> {
            SettingScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
