package woowacourse.shopping.data.model

import com.example.domain.model.CartProduct
import kotlinx.serialization.Serializable

@Serializable
data class CartProductDto(
    val id: Int,
    val quantity: Int,
    val product: ProductDto
)

fun CartProductDto.toDomain(): CartProduct = CartProduct(
    cartProductId = id.toLong(),
    product = product.toDomain(),
    count = quantity,
    isSelected = true
)
