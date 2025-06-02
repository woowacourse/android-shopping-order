package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem

interface CartRepository {
    fun loadPageOfCartItems(
        pageIndex: Int,
        pageSize: Int,
        callback: (List<CartItem>, Boolean, Boolean) -> Unit,
    )

    fun loadAllCartItems(callback: (List<CartItem>) -> Unit)

    fun getAllCartItemsCount(callBack: (Int) -> Unit)

    fun increaseQuantity(
        cartItem: CartItem,
        onResult: () -> Unit,
    )

    fun decreaseQuantity(
        cartItem: CartItem,
        callback: () -> Unit,
    )

    fun addCartItem(
        cartItem: CartItem,
        callback: () -> Unit,
    )

    fun deleteCartItem(
        cartId: Long,
        onResult: () -> Unit,
    )

    fun updateCartItemQuantity(
        cartId: Long,
        quantity: Int,
        onResult: () -> Unit,
    )
}
