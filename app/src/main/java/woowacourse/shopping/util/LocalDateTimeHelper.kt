package woowacourse.shopping.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateTimeHelper {
    fun convertStringToLocalDateTime(timeString: String): LocalDateTime {
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        return LocalDateTime.parse(timeString, formatter)
    }
}
