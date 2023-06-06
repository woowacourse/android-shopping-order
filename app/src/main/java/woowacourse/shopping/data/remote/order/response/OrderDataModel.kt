package woowacourse.shopping.data.remote.order.response

import com.google.gson.annotations.SerializedName

data class OrderDataModel(
    val orderId: Int,
    @SerializedName("thumbnail")
    val imageUrl: String,
    val firstProductName: String,
    val totalCount: Int,
    val spendPrice: Int,
    val createdAt: String
)
