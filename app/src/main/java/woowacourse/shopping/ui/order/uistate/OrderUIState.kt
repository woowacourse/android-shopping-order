package woowacourse.shopping.ui.order.uistate

import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.ui.order.uistate.DiscountPolicyUIState.Companion.toUIState
import woowacourse.shopping.ui.productlist.uistate.ProductUIState
import woowacourse.shopping.ui.productlist.uistate.ProductUIState.Companion.toUIState

data class OrderUIState(
    val id: Long,
    val totalPrice: Int,
    val cart: List<ProductUIState>,
    val discountPolicies: List<DiscountPolicyUIState>
) {
    companion object {
        fun Order.toUIState(): OrderUIState {
            return OrderUIState(
                id,
                totalPrice,
                cart.value.map { it.toUIState() },
                discountPolicies.map { it.toUIState() }
            )
        }
    }
}
