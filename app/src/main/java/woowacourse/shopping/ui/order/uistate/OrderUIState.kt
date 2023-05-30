package woowacourse.shopping.ui.order.uistate

import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.ui.order.uistate.PaymentUIState.Companion.toUIState
import woowacourse.shopping.ui.productlist.uistate.ProductUIState
import woowacourse.shopping.ui.productlist.uistate.ProductUIState.Companion.toUIState

data class OrderUIState(
    val id: Long,
    val cart: List<ProductUIState>,
    val payment: PaymentUIState
) {
    companion object {
        fun Order.toUIState(): OrderUIState {
            return OrderUIState(
                id, cart.value.map { it.toUIState() }, payment.toUIState()
                )
            }
        }
    }
    