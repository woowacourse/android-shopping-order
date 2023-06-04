package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Parcelize
data class OrderPreviewUiModel(
    val orderId: Long,
    val mainName: String,
    val mainImageUrl: String,
    val extraProductCount: Int,
    val date: String,
    val paymentAmount: Int,
) : Parcelable {
    fun toDateFormat(): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val outputFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd")

        val date = LocalDateTime.parse(this.date, inputFormat)
        return date.format(outputFormat)
    }
}
