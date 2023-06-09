package woowacourse.shopping.ui.order.uistate

import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.ui.order.uistate.DiscountPolicyUIState.Companion.toUIState

data class PaymentUIState(
    val discountPolicies: List<DiscountPolicyUIState>
) {
    companion object {
        fun Payment.toUIState(): PaymentUIState {
            return PaymentUIState(discountPolicies.map { it.toUIState() })
        }
    }
}
