package woowacourse.shopping.domain.model

data class PaymentItems(
    private val paymentItems: Set<CartItem>,
) {
    val totalPrice: Long get() = paymentItems.sumOf { it.product.price.amount * it.quantity }
    val totalQuantity: Int get() = paymentItems.sumOf { it.quantity }

    fun getProductIds(): List<Long> = paymentItems.map { it.product.id }

    fun isContain(productId: Long): Boolean = paymentItems.any { it.product.id == productId }

    fun add(item: CartItem): PaymentItems {
        val filtered = paymentItems.filterNot { it.product.id == item.product.id }
        return copy(paymentItems = (filtered + item).toSet())
    }

    fun remove(productId: Long): PaymentItems = copy(paymentItems = paymentItems.filterNot { it.product.id == productId }.toSet())
}
