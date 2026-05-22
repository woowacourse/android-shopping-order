package woowacourse.shopping.domain.coupon

import java.time.LocalDate

data class FreeShippingCoupon(
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val minimumAmount: Int
) : Coupon(
    code = code,
    description = description,
    expirationDate = expirationDate,
) {
    override fun calculateDiscountPrice(orderPrice: Int): Int {
        return 3000
    }

    fun isDiscountable(orderPrice: Int): Boolean = orderPrice >= minimumAmount
}
