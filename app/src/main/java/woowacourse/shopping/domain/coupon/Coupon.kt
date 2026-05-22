package woowacourse.shopping.domain.coupon

import java.time.LocalDate

sealed class Coupon(
    open val code: String,
    open val description: String,
    open val expirationDate: LocalDate,
) {
    fun isExpired(today: LocalDate): Boolean = expirationDate.isBefore(today)

    abstract fun calculateDiscountPrice(orderPrice: Int): Int
}
