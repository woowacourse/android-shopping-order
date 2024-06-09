package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import java.time.LocalDate

sealed interface Coupon {
    val id: Long
    val code: String
    val description: String
    val expirationDate: LocalDate
    val discountType: String

    fun isAvailability(cartItems: List<CartItem>): Boolean
}
