package woowacourse.shopping.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemIdsRequest(
    @SerialName("cartItemIds") val cartItemIds: List<Long>,
)
