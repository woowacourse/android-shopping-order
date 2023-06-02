package woowacourse.shopping.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartGetResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("product")
    val product: ProductGetResponse,
)

@Serializable
data class CartAddRequest(
    @SerialName("productId")
    val productId: Int,
)

@Serializable
data class CartPatchRequest(
    @SerialName("quantity")
    val quantity: Int,
)
