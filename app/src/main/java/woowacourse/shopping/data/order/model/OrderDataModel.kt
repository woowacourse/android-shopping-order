package woowacourse.shopping.data.order.model

import com.google.gson.annotations.SerializedName

data class OrderDataModel(
    val orderId: Int,
    val firstProductName: String,
    val totalQuantity: Int,
    @SerializedName("thumbnail") val imageUrl: String,
    val createdAt: String,
    val spendPrice: Int,
)
