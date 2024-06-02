package woowacourse.shopping.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestOrdersPostDto(
    val cartItemIds: List<Long>,
)
