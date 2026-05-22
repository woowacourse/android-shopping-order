package woowacourse.shopping.domain.coupon

import java.time.LocalDate

data class BuyXGetYCoupon(
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int
) : Coupon(
    code = code,
    description = description,
    expirationDate = expirationDate,
) {
    override fun calculateDiscountPrice(orderPrice: Int): Int {
        return orderPrice
    }

    fun isDiscountable(orderQuantity: Int): Boolean = orderQuantity >= buyQuantity
}
