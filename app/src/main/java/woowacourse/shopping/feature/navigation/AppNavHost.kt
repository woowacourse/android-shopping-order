package woowacourse.shopping.feature.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import woowacourse.shopping.feature.cart.component.CartScreen
import woowacourse.shopping.feature.productdetail.ProductDetailScreen
import woowacourse.shopping.feature.productlist.ProductListScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ProductList,
    ) {
        composable<ProductList> {
            ProductListScreen(
                onProductClick = { productId, recentProductId ->
                    navController.navigate(
                        ProductDetail(
                            id = productId,
                            recentProductId = recentProductId
                        )
                    )
                },
                onCartIconClick = {
                    navController.navigate(Cart)
                },
                activityFinish = {
                    navController.popBackStack()
                },
            )
        }

        composable<ProductDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<ProductDetail>()
            ProductDetailScreen(
                id = route.id,
                activityFinish = { navController.popBackStack() },
                recentProductId = route.recentProductId,
                onClickRecentButton = { id ->
                    navController.navigate(
                        ProductDetail(
                            id = id,
                            recentProductId = route.recentProductId
                        )
                    )
                },
            )
        }

        composable<Cart> {
            CartScreen(
                onCloseClick = { navController.popBackStack() },
                activityFinish = { navController.popBackStack() },
                onToRecommendIntent = { cartContentIds ->

                }
            )
        }
    }
}
