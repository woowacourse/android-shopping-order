package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartProduct

class FreeShippingCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val minimumAmount: Int,
) : Coupon {
    override fun isValid(items: List<CartProduct>): Boolean {
        return items.sumOf { it.totalPrice } >= minimumAmount
    }
}
