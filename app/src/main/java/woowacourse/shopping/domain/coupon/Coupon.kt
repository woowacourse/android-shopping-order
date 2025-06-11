package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.Payment
import woowacourse.shopping.domain.cart.ShoppingCarts
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val expirationDate: LocalDate
    val discountType: String

    fun isUsable(
        today: LocalDateTime,
        order: ShoppingCarts,
        payment: Int,
    ): Boolean

    fun applyToPayment(
        origin: Payment,
        order: ShoppingCarts,
    ): Payment

    fun isExpired(standardDate: LocalDate) = expirationDate.isBefore(standardDate)
}
