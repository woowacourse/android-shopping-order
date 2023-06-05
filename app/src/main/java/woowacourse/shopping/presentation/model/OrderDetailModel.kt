package woowacourse.shopping.presentation.model

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class OrderDetailModel(
    val id: Long,
    val usedPoint: Int,
    val savedPoint: Int,
    val orderDateTime: String,
    val products: List<CartModel>,
) {
    val summaryTitle: String
        get() {
            var summary = products[0].product.title
            summary = if (products.size > 1) summary + " 외 ${products.size - 1}개" else summary
            return summary
        }

    val totalPrice: Int
        get() {
            return products.sumOf { it.count * it.product.price }
        }

    val orderDate: String
        get() {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val localDateTime = LocalDateTime.parse(orderDateTime, formatter)
            val dayOfWeek = getDayOfWeek(localDateTime.dayOfWeek)
            return orderDateTime.substringBeforeLast("T") + dayOfWeek
        }

    private fun getDayOfWeek(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.SUNDAY -> "(일)"
            DayOfWeek.MONDAY -> "(월)"
            DayOfWeek.TUESDAY -> "(화)"
            DayOfWeek.WEDNESDAY -> "(수)"
            DayOfWeek.THURSDAY -> "(목)"
            DayOfWeek.FRIDAY -> "(금)"
            DayOfWeek.SATURDAY -> "(토)"
        }
    }
}
