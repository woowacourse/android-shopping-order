package woowacourse.shopping.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class QuantityRequestDto(
    val quantity: Int,
)