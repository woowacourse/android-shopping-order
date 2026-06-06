package woowacourse.shopping.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun ShoppingNavHost(
    startCartIds: List<Int>? = null,
    onCartIdsConsumed: () -> Unit = {},
) {
    val navController = rememberNavController()

    LaunchedEffect(startCartIds) {
        if (startCartIds != null) {
            navController.navigate(OrderRoute.Order(startCartIds))
            onCartIdsConsumed()
        }
    }

    NavHost(
        navController = navController,
        startDestination = ProductRoute.ProductList,
    ) {
        productNavGraph(
            navController = navController,
            onNavigateToCart = { navController.navigate(CartGraph) },
            onNavigateToSetting = { navController.navigate(SettingRoute.Setting) },
        )

        cartNavGraph(
            navController = navController,
            onNavigateToOrder = { cartIds ->
                navController.navigate(OrderRoute.Order(cartIds))
            },
        )
        orderNavGraph(
            navController = navController,
            onOrderSuccess = {
                navController.navigate(ProductRoute.ProductList) {
                    popUpTo<ProductRoute.ProductList> { inclusive = false }
                    launchSingleTop = true
                }
            },
        )
        settingNavGraph(navController = navController)
    }
}
