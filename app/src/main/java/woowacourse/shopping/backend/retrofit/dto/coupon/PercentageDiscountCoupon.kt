package woowacourse.shopping.backend.retrofit.dto.coupon

import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class AvailableTimeResponse(
    val start: String,
    val end: String
)

data class AvailableTime(
    val start: LocalTime,
    val end: LocalTime,
)

@Serializable
data class PercentageDiscountCoupon(
    val id: Long?,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int?,
    val availableTime: AvailableTimeResponse?,
    val discountType: String?
)
