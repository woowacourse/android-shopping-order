package woowacourse.shopping.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderRequestDto(
    @SerialName("cartItemIds")
    val cartItemIds: List<Int>,
)
