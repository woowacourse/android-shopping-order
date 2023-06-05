package woowacourse.shopping.data.dto.response

import com.example.domain.model.OrderPreview
import com.google.gson.annotations.SerializedName

data class OrderPreviewDto(
    @SerializedName("id")
    val orderId: Long,

    @SerializedName("mainProductName")
    val mainName: String,

    @SerializedName("mainProductImage")
    val mainImageUrl: String,

    @SerializedName("extraProductCount")
    val extraProductCount: Int,

    @SerializedName("date")
    val date: String,

    @SerializedName("price")
    val paymentAmount: Int,
) {
    fun toDomain() =
        OrderPreview(orderId, mainName, mainImageUrl, extraProductCount, date, paymentAmount)
}
