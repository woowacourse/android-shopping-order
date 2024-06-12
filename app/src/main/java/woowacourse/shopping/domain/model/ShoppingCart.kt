package woowacourse.shopping.domain.model

data class ShoppingCart(val items: List<CartItem>) {
    fun getTotalPrice(): Long {
        return items.filter { it.isChecked }.sumOf { it.price * it.quantity }
    }

    fun getTotalQuantity(): Int {
        return items.filter { it.isChecked }.sumOf { it.quantity }
    }

    fun toOrder(): Order {
        val list = items.filter { it.isChecked }
        return Order(list)
    }

    fun selectAll() {
        items.forEach { it.isChecked = true }
    }

    fun unSelectAll() {
        items.forEach { it.isChecked = false }
    }
}
