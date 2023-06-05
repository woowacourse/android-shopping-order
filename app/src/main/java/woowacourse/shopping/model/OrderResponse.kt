package woowacourse.shopping.model

typealias UiOrderResponse = OrderResponse

data class OrderResponse(
    val orderId: Int,
    val orderedProducts: List<UiOrderedProduct>,
    val payment: UiPayment,
)
