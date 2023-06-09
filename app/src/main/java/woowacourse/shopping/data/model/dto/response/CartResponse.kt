package woowacourse.shopping.data.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CartResponse(
    val id: Long,
    val quantity: Int,
    val product: ProductResponse
)
