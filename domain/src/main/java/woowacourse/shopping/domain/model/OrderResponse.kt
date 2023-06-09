package woowacourse.shopping.domain.model

class OrderResponse(
    val orderId: Int,
    val orderedProducts: List<OrderedProduct>,
    val payment: Payment,
)
