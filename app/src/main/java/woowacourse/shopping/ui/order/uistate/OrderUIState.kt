package woowacourse.shopping.ui.order.uistate

import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.ui.shopping.uistate.ProductUIState
import woowacourse.shopping.ui.shopping.uistate.ProductUIState.Companion.toUIState

data class OrderUIState(
    val id: Long,
    val cart: List<ProductUIState>,
    val price: Int
) {
    companion object {
        fun Order.toUIState(): OrderUIState {
            return OrderUIState(
                id, cart.value.map { it.toUIState() }, price
            )
        }
    }
}
