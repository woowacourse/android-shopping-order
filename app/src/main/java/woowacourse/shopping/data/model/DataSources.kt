package woowacourse.shopping.data.model

import com.example.domain.model.CartProduct
import com.example.domain.model.PointInfo
import com.example.domain.model.Price
import com.example.domain.model.Product
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String
)

fun ProductDto.toDomain() = Product(id, name, imageUrl, Price(price))

@Serializable
data class CartProductDto(
    val id: Long,
    val quantity: Int,
    val product: ProductDto
)

fun CartProductDto.toDomain() = CartProduct(id, product.toDomain(), quantity, true)

@Serializable
data class PointDto(
    val currentPoint: Int,
    val toBeExpiredPoint: Int
)

fun PointDto.toDomain() = PointInfo(currentPoint, toBeExpiredPoint)
