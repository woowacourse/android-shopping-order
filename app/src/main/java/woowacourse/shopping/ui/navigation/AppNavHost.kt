package woowacourse.shopping.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import woowacourse.shopping.ui.shopping.ShoppingRouteScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = ShoppingRoute
    ) {
        composable<ShoppingRoute> {
            ShoppingRouteScreen(
                onCartClick = { navController.navigate(CartRoute)},
                onProductClick = { productId ->
                    navController.navigate(ProductDetailRoute(productId))
                }
            )
        }
    }
}