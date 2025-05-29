package woowacourse.shopping.view.cart.state

import woowacourse.shopping.domain.Quantity

data class CartUiState(
    val items: List<CartState> = emptyList(),
    val pageState: PageState = PageState(),
) {
    val totalPrice: Int
        get() = items.filter { it.checked }.sumOf { it.productPrice }

    val checkedProductCount: Int
        get() = items.filter { it.checked }.sumOf { it.cartQuantityValue }

    fun setAllItemsChecked(isChecked: Boolean): CartUiState {
        val result = items.map { it.copy(checked = isChecked) }

        return copy(items = result)
    }

    fun modifyCheckedState(
        cartId: Long,
        check: Boolean,
    ): CartUiState {
        val targetIndex = targetIndex(cartId)
        val targetItem = items[targetIndex]

        val mutableItems = items.toMutableList()
        mutableItems[targetIndex] = targetItem.modifyChecked(check)

        return copy(items = mutableItems)
    }

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
