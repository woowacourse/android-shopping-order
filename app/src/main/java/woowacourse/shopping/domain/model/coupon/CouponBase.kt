package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate

data class CouponBase(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
)
