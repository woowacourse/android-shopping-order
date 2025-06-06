package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate

sealed interface Coupon {
    val id: Long
    val code: String
    val description: String
    val expirationDate: LocalDate

    fun isExpired(today: LocalDate = LocalDate.now()): Boolean = expirationDate.isBefore(today)
}
