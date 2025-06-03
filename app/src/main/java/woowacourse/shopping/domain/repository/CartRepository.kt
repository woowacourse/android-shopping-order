package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem

interface CartRepository {
    fun loadPageOfCartItems(
        pageIndex: Int,
        pageSize: Int,
        callback: (
            cartItems: List<CartItem>,
            isFirstPage: Boolean,
            isLastPage: Boolean,
        ) -> Unit,
    )

    fun loadAllCartItems(callback: (cartItems: List<CartItem>) -> Unit)

    fun findCartItemByProductId(
        id: Long,
        callback: (cartItem: CartItem?) -> Unit,
    )

    fun getAllCartItemsCount(callback: (totalCount: Int) -> Unit)

    fun increaseQuantity(
        cartItem: CartItem,
        callback: () -> Unit,
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
        callback: () -> Unit,
    )

    fun updateCartItemQuantity(
        cartId: Long,
        quantity: Int,
        callback: () -> Unit,
    )
}
