package woowacourse.shopping.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestOrderPostDto(
    val cartItemIds: List<Long>,
)
