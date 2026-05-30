package woowacourse.shopping.domain.model

data class PaymentItems(
    private val paymentItems: Set<CartItem>,
) {
    val totalPrice: Money get() = Money(amount = paymentItems.sumOf { it.product.price.amount * it.quantity })
    val totalQuantity: Int get() = paymentItems.sumOf { it.quantity }

    fun getProductIds(): List<Long> = paymentItems.map { it.product.id }

    fun getPaymentItems() = paymentItems

    fun isContain(productId: Long): Boolean = paymentItems.any { it.product.id == productId }

    fun quantityOf(productId: Long): Int = paymentItems.find { it.product.id == productId }?.quantity ?: 0

    fun add(item: CartItem): PaymentItems {
        val filtered = paymentItems.filterNot { it.product.id == item.product.id }
        return copy(paymentItems = (filtered + item).toSet())
    }

    fun decrease(productId: Long): PaymentItems {
        val existing = paymentItems.find { it.product.id == productId } ?: return this
        return if (existing.quantity == 1) remove(productId) else add(existing.decrease())
    }

    fun remove(productId: Long): PaymentItems = copy(paymentItems = paymentItems.filterNot { it.product.id == productId }.toSet())
}
