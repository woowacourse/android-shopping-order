package woowacourse.shopping.data.dto

typealias OrderResponsesDto = OrderResponses
typealias OrderResponseDto = OrderResponse

data class OrderResponses(
    val orders: List<OrderResponseDto>,
)

data class OrderResponse(
    val orderId: Int,
    val orderedProducts: List<OrderedProductDto>,
    val payment: Payment,
)
