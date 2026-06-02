package woowacourse.shopping.domain.model

data class PaymentItems(
    private val paymentItems: Set<CartItem>,
) {
    val totalPrice: Long get() = paymentItems.sumOf { it.product.price.amount * it.quantity }
    val totalQuantity: Int get() = paymentItems.sumOf { it.quantity }

    fun getItems(): List<CartItem> = paymentItems.toList()
}
