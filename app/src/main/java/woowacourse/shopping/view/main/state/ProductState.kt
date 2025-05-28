package woowacourse.shopping.view.main.state

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.product.Product

data class ProductState(
    val cartId: Long? = null,
    val item: Product,
    val cartQuantity: Quantity,
) {
    val hasCartQuantity: Boolean
        get() = cartQuantity.hasQuantity()

    val cartQuantityValue: Int
        get() = cartQuantity.value

    fun increaseCartQuantity(): ProductState {
        val increasedQuantity = cartQuantity + 1
        return copy(cartQuantity = increasedQuantity)
    }

    fun decreaseCartQuantity(): ProductState {
        val decreasedCartQuantity = cartQuantity - 1
        val newState = copy(cartQuantity = decreasedCartQuantity)

        return newState
    }
}
