package woowacourse.shopping.data.product.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val id: Long,
    val quantity: Int,
)
