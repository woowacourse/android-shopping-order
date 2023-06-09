package woowacourse.shopping.model.data.dto

import com.google.gson.annotations.SerializedName

data class OrderDTO(
    @SerializedName("orderId")
    val orderId: Long,
    @SerializedName("orderPrice")
    val orderPrice: Int,
    @SerializedName("totalAmount")
    val totalAmount: Int,
    @SerializedName("previewName")
    val previewName: String
)
