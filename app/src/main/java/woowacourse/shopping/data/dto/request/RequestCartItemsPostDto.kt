package woowacourse.shopping.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestCartItemsPostDto(
    val cartItemIds:List<Int>
)
