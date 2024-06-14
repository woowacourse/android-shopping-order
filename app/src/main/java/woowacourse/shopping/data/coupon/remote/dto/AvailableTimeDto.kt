package woowacourse.shopping.data.coupon.remote.dto

import woowacourse.shopping.domain.model.coupon.AvailableTime
import java.time.LocalTime

data class AvailableTimeDto(
    val start: String,
    val end: String,
) {
    companion object {
        fun AvailableTimeDto.toDomain() =
            AvailableTime(
                start = LocalTime.parse(start),
                end = LocalTime.parse(end),
            )
    }
}
