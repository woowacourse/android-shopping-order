package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Coupon(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discount: Discount,
) {
    fun isValid(
        products: CartProducts,
        now: LocalDateTime = LocalDateTime.now(),
    ): Boolean = isNotExpired(now.toLocalDate()) && discount.isApplicable(products, now.toLocalTime())

    private fun isNotExpired(now: LocalDate): Boolean = now.isBefore(expirationDate) || now.isEqual(expirationDate)
}
