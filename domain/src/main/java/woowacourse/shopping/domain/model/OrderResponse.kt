package woowacourse.shopping.domain.model

class OrderResponse(
    val orderId: Int,
    val orderedProducts: OrderedProduct,
    val payment: Payment,
)
