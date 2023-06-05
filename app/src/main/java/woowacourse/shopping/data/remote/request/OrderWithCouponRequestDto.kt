package woowacourse.shopping.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderWithCouponRequestDto(
    val cartItemIds: List<Int>,
    val couponId: Int,
)
