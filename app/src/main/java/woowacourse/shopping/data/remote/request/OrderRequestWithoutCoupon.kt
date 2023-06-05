package woowacourse.shopping.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequestWithoutCoupon(
    val cartItemIds: List<Long>,
)
