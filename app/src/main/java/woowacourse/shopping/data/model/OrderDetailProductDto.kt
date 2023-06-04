package woowacourse.shopping.data.model

data class OrderDetailProductDto(
    val usedPoint: Int,
    val products: List<OrderProductDto>
)

data class OrderProductDto(
    val productId: Int,
    val quantity: Int
)
