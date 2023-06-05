package woowacourse.shopping.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderDTO(
    val id: Long,
    val orderProducts: List<OrderProductDTO>,
    val timestamp: String,
    val couponName: String?,
    val originPrice: Int,
    val discountPrice: Int,
    val totalPrice: Int,
)
