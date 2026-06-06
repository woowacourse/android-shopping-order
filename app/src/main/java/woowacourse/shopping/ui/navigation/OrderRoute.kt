package woowacourse.shopping.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import woowacourse.shopping.ui.order.OrderScreen
import woowacourse.shopping.ui.order.OrderViewModel

sealed interface OrderRoute {
    @Serializable
    data class Order(
        val cartIds: List<Int>,
    ) : OrderRoute
}

fun NavGraphBuilder.orderNavGraph(
    navController: NavController,
    onOrderSuccess: () -> Unit,
) {
    composable<OrderRoute.Order> {
        val viewModel: OrderViewModel = viewModel(factory = OrderViewModel.Factory)
        OrderScreen(
            orderViewModel = viewModel,
            onClickClose = { navController.popBackStack() },
            onOrderSuccess = onOrderSuccess,
        )
    }
}
