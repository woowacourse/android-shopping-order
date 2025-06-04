package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.response.Quantity
import woowacourse.shopping.domain.model.CartItem

interface CartRepository {
    fun getCartItems(
        page: Int,
        limit: Int,
        callback: (List<CartItem>, Boolean) -> Unit,
    )

    fun getAllCartItems(callback: (List<CartItem>?) -> Unit)

    fun getAllCartItemsCount(callBack: (Quantity?) -> Unit)

    fun deleteCartItem(
        id: Long,
        callback: (Long) -> Unit,
    )

    fun upsertCartItemQuantity(
        productId: Long,
        cartId: Long? = null,
        quantity: Int,
        callback: (Long) -> Unit,
    )

    fun addCartItem(
        cartItem: CartItem,
        callback: () -> Unit,
    )

    fun increaseCartItem(
        cartItem: CartItem,
        callback: (Long) -> Unit,
    )

    fun decreaseCartItem(
        cartItem: CartItem,
        callback: (Long) -> Unit,
    )

    fun updateCartItemQuantity(
        cartId: Long,
        quantity: Int,
        callback: (Long) -> Unit,
    )
}
