package woowacourse.shopping.view.main.state

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.product.Product

data class ProductState(
    val cartId: Long? = null,
    val item: Product,
    val cartQuantity: Quantity,
) {
    val productId: Long
        get() = item.id

    val hasCartQuantity: Boolean
        get() = cartQuantity.hasQuantity()

    val cartQuantityValue: Int
        get() = cartQuantity.value

    fun modifyQuantity(quantity: Quantity): ProductState {
        return copy(cartQuantity = quantity)
    }

    fun increaseCartQuantity(): ProductState {
        val increasedQuantity = cartQuantity + 1
        return copy(cartQuantity = increasedQuantity)
    }

    fun decreaseCartQuantity(): ProductState {
        val decreasedCartQuantity = cartQuantity - 1
        val newState = copy(cartQuantity = decreasedCartQuantity)

        return newState
    }

    companion object {
        fun of(
            cart: ShoppingCart?,
            product: Product,
        ): ProductState {
            return ProductState(
                cartId = cart?.id,
                item = product,
                cartQuantity = cart?.quantity ?: Quantity(0),
            )
        }
    }
}
