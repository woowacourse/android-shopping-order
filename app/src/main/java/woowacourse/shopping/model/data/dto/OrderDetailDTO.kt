package woowacourse.shopping.model.data.dto

data class OrderDetailDTO(
    val orderItems: List<OrderProductDTO>,
    val originalPrice: Int,
    val usedPoints: Int,
    val orderPrice: Int
)
