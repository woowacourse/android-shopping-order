package woowacourse.shopping.mapper

import woowacourse.shopping.backend.retrofit.dto.coupon.AvailableTimeResponse
import woowacourse.shopping.model.coupon.AvailableTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

fun AvailableTimeResponse.toDomain(): AvailableTime =
    AvailableTime(
        start = LocalTime.parse(start, timeFormatter),
        end = LocalTime.parse(end, timeFormatter),
    )
