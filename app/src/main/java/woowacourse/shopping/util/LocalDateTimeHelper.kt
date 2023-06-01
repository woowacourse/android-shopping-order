package woowacourse.shopping.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateTimeHelper {
    fun convertStringToLocalDateTime(timeString: String): LocalDateTime {
        if (timeString != null) {
            val formatter: DateTimeFormatter =
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
            return LocalDateTime.parse(timeString, formatter)
        }
        return LocalDateTime.now()
    }

    fun convertLocalDateTimeToDateString(localDateTime: LocalDateTime): String {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return localDateTime.format(dateFormatter)
    }
}
