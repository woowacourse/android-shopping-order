package woowacourse.shopping.view.cart.state

import woowacourse.shopping.domain.Quantity

data class CartUiState(
    val items: List<CartState> = emptyList(),
    val pageState: PageState = PageState(),
) {
    fun modifyUiState(newState: CartState): CartUiState {
        val targetIndex = targetIndex(newState.cartId)
        val mutableItems = items.toMutableList()
        mutableItems[targetIndex] = newState

        return copy(items = mutableItems)
    }

    fun increaseCartQuantity(productId: Long): CartState {
        val targetIndex = targetIndex(productId)
        val target = items[targetIndex]
        return target.increaseCartQuantity()
    }

    fun decreaseCartQuantity(productId: Long): CartState {
        val targetIndex = targetIndex(productId)
        val result = items[targetIndex].decreaseCartQuantity()

        if (!result.decreaseCartQuantity().quantity.hasQuantity()) {
            return result.copy(quantity = Quantity(1))
        }

        return result
    }

    private fun targetIndex(productId: Long) = items.indexOfFirst { it.cartId == productId }
}
