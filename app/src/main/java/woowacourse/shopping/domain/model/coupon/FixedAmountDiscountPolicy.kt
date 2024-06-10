package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.Cart

class FixedAmountDiscountPolicy(private val discount: Int?) : DiscountPolicy {
    override fun calculateDiscount(
        totalAmount: Int,
        carts: List<Cart>,
    ): Int {
        discount ?: return totalAmount
        return discount
    }

    companion object {
        const val DELIVERY_FEE_DISCOUNT = 30_000
    }
}
