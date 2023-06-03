package woowacourse.shopping.data.remote.response.order.Individualorder

import com.google.gson.annotations.SerializedName

data class OrderItem(
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("quantity")
    val quantity: Int
)
