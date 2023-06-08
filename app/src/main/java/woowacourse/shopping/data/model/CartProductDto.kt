package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CartProductDto(
    val id: Long,
    val quantity: Int,
    val product: ProductDto
)
