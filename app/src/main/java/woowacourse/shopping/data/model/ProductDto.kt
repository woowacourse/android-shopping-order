package woowacourse.shopping.data.model

import com.example.domain.model.Price
import com.example.domain.model.Product
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int,
    val name: String,
    val price: Int,
    @SerialName("imageUrl")
    val imgUrl: String
)

fun ProductDto.toDomain(): Product = Product(id.toLong(), name, imgUrl, Price(price))
