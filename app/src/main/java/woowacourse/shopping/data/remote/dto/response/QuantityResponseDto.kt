package woowacourse.shopping.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class QuantityResponseDto(
    val quantity: Int,
)
