package woowacourse.shopping.data.dto.response

import com.example.domain.model.OrderProduct
import com.google.gson.annotations.SerializedName

data class OrderProductDto(
    @SerializedName("id")
    val productId: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("singleProductPrice")
    val price: Int,

    @SerializedName("count")
    val count: Int,

    @SerializedName("imageUrl")
    val imageUrl: String,

) {
    fun toDomain() = OrderProduct(productId, name, price, count, imageUrl)
}
