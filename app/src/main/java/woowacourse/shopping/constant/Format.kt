package woowacourse.shopping.constant

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object Format {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")

    fun formatPrice(price: Int): String = String.format(Locale.KOREA, "%,d원", price)

    fun formatDate(date: LocalDate): String = date.format(dateFormatter)
}
