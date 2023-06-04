package woowacourse.shopping.data.remote.request

data class OrderDTO(
    val id: Long,
    val orderProducts: List<OrderProductDTO>,
    val timestamp: String,
    val couponName: String?,
    val originPrice: Int,
    val discountPrice: Int,
    val totalPrice: Int,
)
