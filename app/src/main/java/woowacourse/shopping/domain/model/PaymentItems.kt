package woowacourse.shopping.domain.model

data class PaymentItems(
    private val paymentItems: Set<CartItem>,
) {
    val totalPrice: Long get() = paymentItems.sumOf { it.product.price.amount * it.quantity }
    val totalQuantity: Int get() = paymentItems.sumOf { it.quantity }

    fun isContain(productId: Long): Boolean = paymentItems.any { it.product.id == productId }

    fun add(item: CartItem): PaymentItems = copy(paymentItems = paymentItems + item)

    fun remove(productId: Long): PaymentItems = copy(paymentItems = paymentItems.filterNot { it.product.id == productId }.toSet())
}
