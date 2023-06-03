package woowacourse.shopping.data.model

data class OrderRequestDto(
    val usedPoint: Int,
    val products: List<OrderRequestProductDto>
)
