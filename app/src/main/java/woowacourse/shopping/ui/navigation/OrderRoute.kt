package woowacourse.shopping.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import woowacourse.shopping.ui.order.OrderScreen

sealed interface OrderRoute {
    @Serializable
    data class Order(
        val cartIds: List<Int>,
        val fromNotification: Boolean = false,
    ) : OrderRoute
}

fun NavGraphBuilder.orderNavGraph(
    navController: NavController,
    onOrderSuccess: () -> Unit,
) {
    composable<OrderRoute.Order> {
        OrderScreen(
            onClickClose = { navController.popBackStack() },
            onOrderSuccess = onOrderSuccess,
        )
    }
}
