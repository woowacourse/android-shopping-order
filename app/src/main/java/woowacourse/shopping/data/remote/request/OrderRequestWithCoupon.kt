package woowacourse.shopping.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequestWithCoupon(
    val cartItemsIds: List<Long>,
    val couponId: Long,
)
