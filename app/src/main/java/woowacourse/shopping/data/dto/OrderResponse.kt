package woowacourse.shopping.data.dto

typealias OrderResponseDto = OrderResponse

data class OrderResponse(
    val orderId: Int,
    val orderedProducts: OrderedProductDto,
    val payment: Payment,
)
