package woowacourse.shopping.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val DATE_PARSE_PATTERN = "yyyy-MM-dd"
private const val DATE_FORMAT_PATTERN = "yyyy년 MM월 dd일"
private const val TIME_PARSE_PATTERN = "HH:mm:SS"

fun convertStringToLocalDate(date: String): LocalDate {
    return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_PARSE_PATTERN))
}

fun convertLocalDateToFormatString(localDate: LocalDate): String {
    return localDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN))
}

fun convertStringToLocalTime(time: String): LocalTime {
    return LocalTime.parse(time, DateTimeFormatter.ofPattern(TIME_PARSE_PATTERN))
}
