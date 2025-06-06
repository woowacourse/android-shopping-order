package woowacourse.shopping.domain.coupon

import java.time.LocalDate

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val expirationDate: LocalDate
    val discountType: String

    fun isExpired(standardDate: LocalDate) = standardDate > expirationDate
}
