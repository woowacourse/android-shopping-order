package woowacourse.shopping.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseCartItemCountsGetDto(
    val quantity: Int,
)
