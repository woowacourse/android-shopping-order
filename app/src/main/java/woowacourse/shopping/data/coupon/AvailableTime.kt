package woowacourse.shopping.data.coupon

import woowacourse.shopping.data.dto.response.ResponseCouponDto
import java.time.LocalTime

data class AvailableTime(
    val start: LocalTime,
    val end: LocalTime,
)

fun ResponseCouponDto.AvailableTime.toModel() =
    AvailableTime(
        this.start.toLocalTime(),
        this.end.toLocalTime(),
    )
