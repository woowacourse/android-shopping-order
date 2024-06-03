package woowacourse.shopping.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestCartItemPostDto(
    val productId: Long,
    val quantity: Int,
)
