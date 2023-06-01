package woowacourse.shopping.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartGetResponse(
    val id: Int,
    val quantity: Int,
    val product: ProductGetResponse,
)

@Serializable
data class CartAddRequest(
    val productId: Int,
)

@Serializable
data class CartPatchRequest(
    val quantity: Int,
)
