package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Parcelize
data class OrderDetailUiModel(
    val priceBeforeDiscount: Int,
    val priceAfterDiscount: Int,
    val date: String,
    val orderItems: List<OrderProductUiModel>,
) : Parcelable {
    fun toDateFormat(): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val outputFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd")

        val date = LocalDateTime.parse(this.date, inputFormat)
        return date.format(outputFormat)
    }
}
