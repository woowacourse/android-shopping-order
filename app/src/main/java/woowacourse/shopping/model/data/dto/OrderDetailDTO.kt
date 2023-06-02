package woowacourse.shopping.model.data.dto

data class OrderDetailDTO(
    val orderItems: List<OrderDTO>,
    val originalPrice: Int,
    val usedPoints: Int,
    val orderPrice: Int
)
