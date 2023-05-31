package woowacourse.shopping.data.order.model

import com.google.gson.annotations.SerializedName

data class OrderDataModel(
    val orderId: Int,
    @SerializedName("thumbnail") val imageUrl: String,
    val orderDate: String,
    val sendPrice: Int,
)
