package woowacourse.shopping.view.cart.state

import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.view.main.state.ProductState

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

    fun findCart(cartId: Long) = items.find { it.cartId == cartId } ?: throw IllegalArgumentException()

    fun addCart(
        cartId: Long,
        productState: ProductState,
    ): CartUiState {
        val shoppingCart = ShoppingCart(cartId, productState.item, productState.cartQuantity)
        val cartState = CartState(shoppingCart, true)
        return copy(items = items + cartState)
    }

    fun deleteCart(cartId: Long): CartUiState {
        val targetIndex = items.indexOfFirst { it.cartId == cartId }
        val frontItems = items.subList(0, targetIndex)
        val backItems = items.subList(targetIndex + 1, items.size)
        return copy(items = frontItems + backItems)
    }

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

    fun increaseCartQuantity(cartId: Long): CartUiState {
        return updateItem(cartId) { it.increaseCartQuantity() }
    }

    fun decreaseCartQuantity(cartId: Long): CartUiState {
        return updateItem(cartId) { it.decreaseCartQuantity(1) }
    }

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
