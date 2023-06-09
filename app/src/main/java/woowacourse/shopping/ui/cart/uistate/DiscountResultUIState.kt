package woowacourse.shopping.ui.cart.uistate

import woowacourse.shopping.domain.DiscountResult

data class DiscountResultUIState(
    val policyName: String,
    val discountRate: Double,
    val discountPrice: Int
) {
    companion object {
        fun from(discountResult: DiscountResult): DiscountResultUIState {
            return DiscountResultUIState(
                discountResult.discountPolicy.toString(),
                discountResult.discountRate,
                discountResult.discountPrice
            )
        }

        fun from(discountResult: woowacourse.shopping.data.discount.DiscountResult): DiscountResultUIState {
            return DiscountResultUIState(
                discountResult.discountPolicy.toString(),
                discountResult.discountRate,
                discountResult.discountPrice
            )
        }
    }
}
