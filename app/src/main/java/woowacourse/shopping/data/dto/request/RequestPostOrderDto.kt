package woowacourse.shopping.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestPostOrderDto(
    val cartItemIds: List<Long>
)
