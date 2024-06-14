package woowacourse.shopping.presentation.converter

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val DATE_FORMAT_PATTERN = "yyyy년 MM월 dd일"
private const val TIME_FORMAT_PATTERN = "HH:mm"

fun convertLocalDateToFormatString(localDate: LocalDate): String {
    return localDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN))
}

fun convertLocalTimeToFormatString(localTime: LocalTime): String {
    return localTime.format(DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN))
}
