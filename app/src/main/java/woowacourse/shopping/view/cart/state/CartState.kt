package woowacourse.shopping.view.cart.state

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.product.Price

data class CartState(
    val cartId: Long,
    val name: String,
    val imgUrl: String,
    val price: Price,
    val quantity: Quantity,
) {
    val priceValue: Int
        get() = price.value

    val cartQuantityValue: Int
        get() = quantity.value

    val hasQuantity: Boolean
        get() = quantity.hasQuantity()

    val productPrice: Int
        get() = priceValue * cartQuantityValue

    fun increaseCartQuantity(): CartState {
        val increasedQuantity = quantity + 1

        return copy(quantity = increasedQuantity)
    }

    fun decreaseCartQuantity(): CartState {
        val decreasedCartQuantity = quantity - 1
        val newState = copy(quantity = decreasedCartQuantity)

        return newState
    }
}

fun ShoppingCart.toCartState(): CartState {
    return CartState(
        cartId = id,
        name = product.name,
        imgUrl = product.imgUrl,
        price = Price(product.priceValue),
        quantity = quantity,
    )
}
