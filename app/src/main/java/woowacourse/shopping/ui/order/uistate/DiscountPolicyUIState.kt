package woowacourse.shopping.ui.order.uistate

import woowacourse.shopping.domain.order.DiscountPolicy

data class DiscountPolicyUIState(
    val name: String,
    val discountRate: Double,
    val discountPrice: Int
) {

    companion object {
        fun DiscountPolicy.toUIState(): DiscountPolicyUIState {
            return DiscountPolicyUIState(
                name, discountRate, discountPrice
            )
        }
    }
}
