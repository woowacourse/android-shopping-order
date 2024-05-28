package woowacourse.shopping.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestCartItemsGetDto(
    val productId: Int,
    val quantity: Int,
)
