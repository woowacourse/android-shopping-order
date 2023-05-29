package woowacourse.shopping.data.model.dto.response

import com.example.domain.model.Price
import com.example.domain.model.Product
import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("imageUrl")
    val imageUrl: String
) {
    fun toDomain(): Product {
        return Product(id, name, imageUrl, Price(price))
    }
}
