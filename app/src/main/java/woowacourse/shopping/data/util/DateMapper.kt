package woowacourse.shopping.data.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun String.toLocalTime(): LocalTime {
    return runCatching { LocalTime.parse(this, DateTimeFormatter.ofPattern("HH:mm:ss")) }
        .getOrElse { throw IllegalArgumentException(INVALID_LOCAL_TIME_PARSE) }
}

fun String.toLocalDate(): LocalDate {
    return runCatching { LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
        .getOrElse { throw IllegalArgumentException(INVALID_LOCAL_DATE_PARSE) }
}

private const val INVALID_LOCAL_TIME_PARSE = "변환할 시간이 올바르지 않습니다."
private const val INVALID_LOCAL_DATE_PARSE = "변환할 날짜가 올바르지 않습니다."
