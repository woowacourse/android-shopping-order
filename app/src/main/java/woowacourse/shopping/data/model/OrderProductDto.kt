package woowacourse.shopping.data.model

import com.example.domain.model.OrderProduct
import kotlinx.serialization.Serializable

@Serializable
data class OrderProductDto(
    val quantity: Int,
    val product: ProductDto
)

fun OrderProductDto.toDomain() = OrderProduct(
    quantity, product.toDomain()
)
