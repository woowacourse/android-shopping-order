package woowacourse.shopping.core.formatter

import java.time.LocalDate

fun LocalDate.toFormattedDateString(): String = "${year}년 ${monthValue}월 ${dayOfMonth}일"
