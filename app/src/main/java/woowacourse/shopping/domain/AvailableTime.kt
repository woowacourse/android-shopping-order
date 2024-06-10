package woowacourse.shopping.domain

import java.time.LocalTime

data class AvailableTime(
    val start: LocalTime,
    val end: LocalTime,
)
