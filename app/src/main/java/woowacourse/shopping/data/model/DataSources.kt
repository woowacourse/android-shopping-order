package woowacourse.shopping.data.model

import com.example.domain.model.CartProduct
import com.example.domain.model.PointInfo
import com.example.domain.model.Price
import com.example.domain.model.Product

data class ProductDto(
    val id: Long,
    val name: String,
    val price: Price,
    val imageUrl: String
)

fun ProductDto.toDomain() = Product(id, name, imageUrl, price)

data class CartProductDto(
    val id: Long,
    val quantity: Int,
    val product: ProductDto
)

fun CartProductDto.toDomain() = CartProduct(id, product.toDomain(), quantity, true)

data class PointDto(
    val currentPoint: Int,
    val toBeExpiredPoint: Int
)

fun PointDto.toDomain() = PointInfo(currentPoint, toBeExpiredPoint)
