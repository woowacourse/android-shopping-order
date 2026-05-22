package woowacourse.shopping.domain.coupon

import java.time.LocalDate

data class FixedDiscountCoupon(
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val discount: Int,
    val minimumAmount: Int
) : Coupon(
    code = code,
    description = description,
    expirationDate = expirationDate,
) {
    override fun calculateDiscountPrice(orderPrice: Int): Int {
        return orderPrice - discount
    }

    fun isDiscountable(orderPrice: Int): Boolean = orderPrice >= minimumAmount
}
