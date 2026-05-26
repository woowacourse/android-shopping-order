package woowacourse.shopping.data.remote.cart

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequestDto(
    val cartItemIds: List<Long>,
)
