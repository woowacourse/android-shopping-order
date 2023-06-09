package woowacourse.shopping.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderWithoutCouponRequestDto(
    val cartItemIds: List<Int>,

)
