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
                onCartClick = {
                    navController.navigate(Cart)
                },
                onSettingClick = {
                    navController.navigate(Setting)
                },
                onDetailClick = {
                    navController.navigate(Detail(productId = it))
                },
            )
        }

        composable<Detail> { backStackEntry ->
            val route = backStackEntry.toRoute<Detail>()
            DetailRoute(
                productId = route.productId,
                onDismiss = {
                    navController.navigate(Shopping) {
                        popUpTo<Shopping> {
                            inclusive = true
                        }
                    }
                },
                onCartClick = {
                    navController.navigate(Cart)
                },
                onDetailClick = {
                    navController.navigate(Detail(productId = it))
                },
            )
        }

        composable<Cart> {
            CartRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onPaymentClick = {
                    navController.navigate(Payment(selectedCartItemIds = it))
                },
            )
        }

        composable<Payment> { backStackEntry ->
            val route = backStackEntry.toRoute<Payment>()
            PaymentRoute(
                selectedCartItemIds = route.selectedCartItemIds,
                onBackClick = {
                    navController.popBackStack()
                },
                onPaymentSuccess = {
                    navController.navigate(Shopping) {
                        popUpTo<Shopping> {
                            inclusive = true
                        }
                    }
                },
            )
        }

        composable<Setting> {
            SettingRoute(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}
