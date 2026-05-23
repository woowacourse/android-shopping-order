package woowacourse.shopping.model

data class Order(
    val items: List<OrderItem>
) {
    fun totalAmount(): Price =
        Price(items.sumOf { item -> item.unitPrice.toInt() * item.quantity })
}