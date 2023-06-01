package woowacourse.shopping.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class CartProductEntity(
    val id: Int,
    val quantity: Int = 0,
    val product: ProductEntity
)