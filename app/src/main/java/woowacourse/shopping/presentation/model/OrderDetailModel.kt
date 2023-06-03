package woowacourse.shopping.presentation.model

import woowacourse.shopping.presentation.mapper.toModel
import java.time.DayOfWeek
import java.time.LocalDate

data class OrderDetailModel(
    val id: Long,
    val usedPoint: Int,
    val savedPoint: Int,
    val orderDateTime: String,
    val products: List<CartProductModel>,
) {
    fun getSummaryTitle(): String {
        var summary = products[0].product.title
        summary = if (products.size > 1) summary + " 외 ${products.size - 1}개" else summary
        return summary
    }

    fun getOrderDate(): String {
        val date = toModel().getOrderDate()
        return "$date (${getDayOfWeek(date)})"
    }

    private fun getDayOfWeek(date: LocalDate): String =
        when (date.dayOfWeek!!) {
            DayOfWeek.MONDAY -> "월"
            DayOfWeek.TUESDAY -> "화"
            DayOfWeek.WEDNESDAY -> "수"
            DayOfWeek.THURSDAY -> "목"
            DayOfWeek.FRIDAY -> "금"
            DayOfWeek.SATURDAY -> "토"
            DayOfWeek.SUNDAY -> "일"
        }
}
