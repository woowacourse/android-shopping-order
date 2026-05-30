package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

sealed interface OrderRoute {
    @Serializable
    data class Order(
        val cartIds: List<Int>,
    ) : OrderRoute
}
