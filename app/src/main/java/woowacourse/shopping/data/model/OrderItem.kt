package woowacourse.shopping.data.model

typealias DataOrderItem = OrderItem

class OrderItem(
    val count: DataCount,
    val product: DataProduct
)
