package woowacourse.shopping.util

import java.time.format.DateTimeFormatter

object LocalDateTimeFormatter {
    val hyphenColonFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
}
