package woowacourse.shopping.data.cart.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductInsertCartRequest(
    val productId: Long,
    val quantity: Int,
)
