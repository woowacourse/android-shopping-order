package woowacourse.shopping.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestOrderPostDto(
    val cartItemIds: List<Long>,
)
