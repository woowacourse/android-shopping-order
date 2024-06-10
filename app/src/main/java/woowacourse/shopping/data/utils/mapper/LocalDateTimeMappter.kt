package woowacourse.shopping.data.utils.mapper

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun String.toLocalTime(): LocalTime = LocalTime.parse(this, DateTimeFormatter.ofPattern("HH:mm:ss"))

fun String.toLocalDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
