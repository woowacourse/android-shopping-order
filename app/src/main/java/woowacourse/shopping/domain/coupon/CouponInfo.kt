package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.TimeRange
import java.time.LocalDate

data class CouponInfo(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discount: Int?,
    val minimumAmount: Int?,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val availableTime: TimeRange?,
)
