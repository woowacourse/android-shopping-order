package woowacourse.shopping.ui.util

import java.time.LocalDateTime

fun LocalDateTime.formattedDate(): String = "${year}년 ${monthValue}월 ${dayOfMonth}일"
