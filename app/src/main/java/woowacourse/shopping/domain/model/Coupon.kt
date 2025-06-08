package woowacourse.shopping.domain.model

import java.time.LocalDate

data class Coupon(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discountType: Discount,
)
