package woowacourse.shopping.domain.model.coupons

import woowacourse.shopping.domain.model.Cart
import java.time.LocalDate

sealed interface Coupon {
    val id: Long
    val code: String
    val description: String
    val expirationDate: LocalDate
    val discountType: String

    fun calculateDiscountRate(carts: List<Cart>): Int
}
