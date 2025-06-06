package woowacourse.shopping.view.order

import java.time.LocalDateTime

data class CouponState(
    val title: String,
    val explanation: LocalDateTime,
    val minimumOrderPrice: Int?,
)
