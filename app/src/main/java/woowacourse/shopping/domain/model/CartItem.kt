package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.repository.CartRepository.Companion.DEFAULT_QUANTITY

data class CartItem(
    val id: Int,
    val productId: Int,
    val productName: String,
    val price: Int,
    val imageUrl: String,
    val quantity: Int,
) {
    fun plusQuantity(): CartItem {
        return copy(quantity = quantity + 1)
    }

    fun minusQuantity(): CartItem {
        return copy(quantity = (this.quantity - 1).coerceAtLeast(DEFAULT_QUANTITY))
    }

    fun totalPrice(): Int = price * quantity
}
