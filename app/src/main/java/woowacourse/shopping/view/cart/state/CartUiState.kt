package woowacourse.shopping.view.cart.state

data class CartUiState(
    val items: List<CartState> = emptyList(),
    val pageState: PageState = PageState(),
    val allChecked: Boolean = false,
) {
    val totalPrice: Int
        get() = items.filter { it.checked }.sumOf { it.totalPrice }

    val checkedProductCount: Int
        get() = items.filter { it.checked }.sumOf { it.quantity }

    val cartIds: List<Long>
        get() = items.map { it.cart.productId }

    fun setAllItemsChecked(isChecked: Boolean): CartUiState {
        val updatedItems = items.map { it.modifyChecked(isChecked) }
        return copy(items = updatedItems, allChecked = isChecked)
    }

    fun modifyCheckedState(
        cartId: Long,
        check: Boolean,
    ): CartUiState {
        return updateItem(cartId) { it.modifyChecked(check) }
    }

    fun increaseCartQuantity(productId: Long): CartUiState {
        return updateItem(productId) { it.increaseCartQuantity() }
    }

    fun decreaseCartQuantity(productId: Long): CartUiState {
        return updateItem(productId) { it.decreaseCartQuantity(1) }
    }

    private fun updateItem(
        productId: Long,
        transform: (CartState) -> CartState,
    ): CartUiState {
        val index = items.indexOfFirst { it.cartId == productId }
        if (index == -1) return this

        val updatedItems = items.toMutableList()
        updatedItems[index] = transform(items[index])
        return copy(items = updatedItems)
    }
}
