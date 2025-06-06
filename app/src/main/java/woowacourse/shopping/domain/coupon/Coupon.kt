package woowacourse.shopping.domain.coupon

import java.time.LocalDateTime

data class Coupon(
    val title: String,
    val explanation: LocalDateTime,
    val minimumOrderPrice: Int?,
)
