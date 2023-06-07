package woowacourse.shopping.data.model.dto.response

import com.example.domain.model.OrderProduct
import com.example.domain.model.Price
import com.example.domain.model.Product
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
    val imageUrl: String
) {
    fun toDomain(): OrderProduct {
        return OrderProduct(Product(productId, name, imageUrl, Price(price)), count)
    }
}
