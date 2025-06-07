package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartProduct

data class FixedCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val discount: Int,
    val minimumAmount: Int,
) : Coupon {
    override fun isValid(items: List<CartProduct>): Boolean {
        return items.sumOf { it.totalPrice } >= minimumAmount
    }

    fun calculateDiscountAmount(): Int = -discount
}
