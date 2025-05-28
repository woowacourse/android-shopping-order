package woowacourse.shopping.view.main.state

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.product.Product

data class ProductState(
    val item: Product,
    val cartQuantity: Quantity,
) {
    val productPrice: Int
        get() = item.priceValue * cartQuantityValue

    val hasCartQuantity: Boolean
        get() = cartQuantity.hasQuantity()

    val cartQuantityValue: Int
        get() = cartQuantity.value

    fun increaseCartQuantity(): IncreaseState {
        val increasedQuantity = cartQuantity + 1
        val canIncrease = item.canIncrease(increasedQuantity)

        return if (canIncrease) {
            IncreaseState.CanIncrease(copy(cartQuantity = increasedQuantity))
        } else {
            IncreaseState.CannotIncrease(item.quantityValue)
        }
    }

    fun increaseCartQuantity2(): ProductState {
        val increasedQuantity = cartQuantity + 1
        return copy(cartQuantity = increasedQuantity)
    }

    fun decreaseCartQuantity(): ProductState {
        val decreasedCartQuantity = cartQuantity - 1
        val newState = copy(cartQuantity = decreasedCartQuantity)

        return newState
    }
}
