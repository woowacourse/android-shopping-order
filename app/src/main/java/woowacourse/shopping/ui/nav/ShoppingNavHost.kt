package woowacourse.shopping.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import woowacourse.shopping.ui.cart.CartRoute
import woowacourse.shopping.ui.detail.DetailRoute
import woowacourse.shopping.ui.payment.PaymentRoute
import woowacourse.shopping.ui.setting.SettingRoute
import woowacourse.shopping.ui.shopping.ShoppingRoute

@Composable
fun ShoppingNavHost(paymentCartItemIds: List<String>? = null) {
    val navController = rememberNavController()
    val startDestination =
        if (paymentCartItemIds == null) Shopping else Payment(selectedCartItemIds = paymentCartItemIds)

    NavHost(
        navController = navController,
        startDestination = startDestination,
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

        composable<Payment> { backStackEntry ->
            val route = backStackEntry.toRoute<Payment>()
            PaymentRoute(
                selectedCartItemIds = route.selectedCartItemIds,
                navController = navController,
            )
        }

        composable<Setting> {
            SettingRoute(
                navController = navController,
            )
        }
    }
}
