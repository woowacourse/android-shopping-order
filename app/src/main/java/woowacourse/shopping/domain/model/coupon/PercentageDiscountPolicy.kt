package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.Cart

class PercentageDiscountPolicy(private val percentage: Int?) : DiscountPolicy {
    override fun calculateDiscount(
        totalAmount: Int,
        carts: List<Cart>,
    ): Int {
        percentage ?: return totalAmount
        return (totalAmount * percentage * 0.01).toInt()
    }
}
