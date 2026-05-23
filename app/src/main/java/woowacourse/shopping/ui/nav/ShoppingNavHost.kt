package woowacourse.shopping.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import woowacourse.shopping.ui.cart.CartRoute
import woowacourse.shopping.ui.detail.DetailRoute
import woowacourse.shopping.ui.shopping.ShoppingRoute

@Composable
fun ShoppingNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Shopping,
    ) {
        composable<Shopping> {
            ShoppingRoute(
                navController = navController,
            )
        }

        composable<Detail> { backStackEntry ->
            val route = backStackEntry.toRoute<Detail>()
            DetailRoute(
                productId = route.productId,
                navController = navController,
            )
        }

        composable<Cart> {
            CartRoute(
                navController = navController,
            )
        }
    }
}
