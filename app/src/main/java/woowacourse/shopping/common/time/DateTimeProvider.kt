package woowacourse.shopping.common.time

import java.time.LocalDate
import java.time.LocalTime

interface DateTimeProvider {
    fun currentDate(): LocalDate

    fun currentTime(): LocalTime
}

class SystemDateTimeProvider : DateTimeProvider {
    override fun currentDate(): LocalDate = LocalDate.now()

    override fun currentTime(): LocalTime = LocalTime.now()
}
