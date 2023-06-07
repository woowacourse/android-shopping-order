package woowacourse.shopping.data.datasource.response

import com.google.gson.annotations.SerializedName

data class OrderProductEntity(
    @SerializedName("productId")
    val productId: Long,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("quantity")
    val quantity: Long,
    @SerializedName("price")
    val price: Long,
    @SerializedName("imageUrl")
    val imageUrl: String,
)
