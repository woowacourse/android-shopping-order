package woowacourse.shopping.view.cart.state

import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.view.main.state.ProductState

data class CartUiState(
    val items: List<CartState> = emptyList(),
    val pageState: PageState = PageState(),
    val isFetching: Boolean = true,
) {
    val checkedCart: List<CartState>
        get() = items.filter { it.checked }

    val hasPurchaseCart: Boolean
        get() = checkedCart.isNotEmpty()

    val purchaseCart
        get() = checkedCart.map { it.cart }

    val totalPrice: Int
        get() = checkedCart.sumOf { it.totalPrice }

    val checkedProductCount: Int
        get() = checkedCart.sumOf { it.quantity }

    val cartIds: List<Long>
        get() = items.map { it.cart.productId }

    val allChecked: Boolean = items.all { it.checked }

    fun findCart(cartId: Long) = items.find { it.cartId == cartId } ?: throw IllegalArgumentException()

    fun addCart(
        cartId: Long,
        productState: ProductState,
    ): CartUiState {
        val shoppingCart = ShoppingCart(cartId, productState.item, productState.cartQuantity)
        val cartState = CartState(shoppingCart, true)
        val mutableItems = items.toMutableList()
        mutableItems.add(cartState)
        return copy(items = mutableItems)
    }

    fun deleteCart(cartId: Long): CartUiState {
        val targetIndex = items.indexOfFirst { it.cartId == cartId }
        val mutableItems = items.toMutableList()
        mutableItems.removeAt(targetIndex)
        return copy(items = mutableItems)
    }

    fun setAllItemsChecked(isChecked: Boolean): CartUiState {
        val updatedItems = items.map { it.modifyChecked(isChecked) }
        return copy(items = updatedItems)
    }

    fun modifyCheckedState(
        cartId: Long,
        check: Boolean,
    ): CartUiState {
        return updateItem(cartId) { it.modifyChecked(check) }
    }

    fun increaseCartQuantity(cartId: Long): CartUiState {
        return updateItem(cartId) { it.increaseCartQuantity() }
    }

    fun decreaseCartQuantity(cartId: Long): CartUiState {
        return updateItem(cartId) { it.decreaseCartQuantity(1) }
    }

    fun toggleFetching() = copy(isFetching = !isFetching)

    private fun updateItem(
        cartId: Long,
        transform: (CartState) -> CartState,
    ): CartUiState {
        val index = items.indexOfFirst { it.cartId == cartId }
        if (index == -1) return this

        val updatedItems = items.toMutableList()
        updatedItems[index] = transform(items[index])
        return copy(items = updatedItems)
    }
}
