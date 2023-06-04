package woowacourse.shopping.model

typealias UiOrderResponse = OrderResponse

class OrderResponse(
    val orderId: Int,
    val orderedProducts: List<UiOrderedProduct>,
    val payment: UiPayment,
)
