package woowacourse.shopping.ui.orderlist.uistate

import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.ui.productlist.uistate.ProductUIState
import woowacourse.shopping.ui.productlist.uistate.ProductUIState.Companion.toUIState

data class OrderUIState(
    val id: Long,
    val cart: List<ProductUIState>
) {
    companion object {
        fun Order.toUIState(): OrderUIState {
            return OrderUIState(id, cart.value.map { it.toUIState() })
        }
    }
}
