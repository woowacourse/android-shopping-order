package woowacourse.shopping.data.model.request

import kotlinx.serialization.SerialName

data class CartItemIdsRequest(
    @SerialName("cartItemIds") val cartItemIds: List<Long>,
)
