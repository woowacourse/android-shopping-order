package woowacourse.shopping.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import woowacourse.shopping.presentation.cart.CartRoute
import woowacourse.shopping.presentation.detail.DetailRoute
import woowacourse.shopping.presentation.payment.PaymentRoute
import woowacourse.shopping.presentation.recommend.RecommendRoute
import woowacourse.shopping.presentation.shopping.ShoppingRoute

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ShoppingScreen,
    ) {
        composable<ShoppingScreen> {
            ShoppingRoute(navController = navController)
        }

        composable<DetailScreen> { backStackEntry ->
            val route: DetailScreen = backStackEntry.toRoute()

            DetailRoute(
                productId = route.productId,
                isFromLastSeen = route.isFromLastSeen,
                navController = navController,
            )
        }

        composable<CartScreen> {
            CartRoute(
                navController = navController,
            )
        }

        composable<RecommendScreen> { backStackEntry ->
            val route: RecommendScreen = backStackEntry.toRoute()

            RecommendRoute(
                productIds = route.productIds,
                navController = navController,
            )
        }

        composable<PaymentScreen> { backStackEntry ->
            val route: PaymentScreen = backStackEntry.toRoute()

            PaymentRoute(
                orderAmount = route.orderAmount,
                navController = navController,
            )
        }
    }
}
