package woowacourse.shopping.view.cart.state

import woowacourse.shopping.domain.cart.ShoppingCart

data class CartState(
    val cart: ShoppingCart,
    val checked: Boolean,
) {
    val name: String = cart.product.name

    val cartId: Long = cart.id

    val quantity: Int = cart.quantity.value

    val price: Int = cart.product.priceValue

    val imgUrl: String = cart.product.imgUrl

    val hasQuantity: Boolean
        get() = cart.quantity.hasQuantity()

    val totalPrice: Int
        get() = quantity * price

    fun modifyChecked(checked: Boolean): CartState {
        return copy(checked = checked)
    }

    fun increaseCartQuantity(): CartState {
        val updatedCart = cart.increasedQuantity()
        return copy(cart = updatedCart)
    }

    fun decreaseCartQuantity(minQuantity: Int): CartState {
        val updatedCart = cart.withDecreasedQuantityOrMin(minQuantity)
        return copy(cart = updatedCart)
    }
}
