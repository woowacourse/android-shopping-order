package woowacourse.shopping.domain.model

data class Order(
    val orderId: Long,
    val orderedDateTime: String,
    val products: List<OrderedProduct>,
    val totalPrice: Int,
) {
    data class OrderedProduct(
        val product: Product,
        val quantity: Int,
    )
}
