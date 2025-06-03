package woowacourse.shopping.view.cart.state

import woowacourse.shopping.domain.cart.ShoppingCart

data class CartState(
    val cart: ShoppingCart,
    val checked: Boolean,
) {
    val name: String
        get() = cart.product.name

    val cartId: Long
        get() = cart.id

    val quantity: Int
        get() = cart.quantity.value

    val price: Int
        get() = cart.product.priceValue

    val imgUrl: String
        get() = cart.product.imgUrl

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
