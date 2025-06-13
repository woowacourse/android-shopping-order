package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartItemDataSource
import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Page

class CartRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource,
) : CartRepository {
    override suspend fun loadCartItemByProductId(id: Long): CartItem? {
        val cartItems = cartItemDataSource.fetchPageOfCartItems(0, Int.MAX_VALUE).items
        return cartItems.find { cartItem -> cartItem.product.id == id }
    }

    override suspend fun loadPageOfCartItems(
        pageIndex: Int,
        pageSize: Int,
    ): Page<CartItem> {
        return cartItemDataSource.fetchPageOfCartItems(pageIndex, pageSize)
    }

    override suspend fun loadAllCartItems(): List<CartItem> {
        return cartItemDataSource.fetchPageOfCartItems(0, Int.MAX_VALUE).items
    }

    override suspend fun loadTotalCartCount(): Int {
        return cartItemDataSource.fetchCartItemsCount()
    }

    override suspend fun increaseQuantity(cartItem: CartItem) {
        cartItemDataSource.updateCartItem(
            cartItem.cartId,
            cartItem.quantity + 1,
        )
    }

    override suspend fun decreaseQuantity(cartItem: CartItem) {
        cartItemDataSource.updateCartItem(
            cartItem.cartId,
            cartItem.quantity - 1,
        )
    }

    override suspend fun addCartItem(cartItem: CartItem): CartItem? {
        cartItemDataSource.submitCartItem(cartItem.product.id, cartItem.quantity)
        return loadCartItemByProductId(cartItem.product.id)
    }

    override suspend fun addOrUpdateCartItem(cartItem: CartItem) {
        loadCartItemByProductId(cartItem.product.id).let { existingItem ->
            if (existingItem == null) {
                addCartItem(cartItem)
            } else {
                updateCartItemQuantity(cartItem.cartId, existingItem.quantity + cartItem.quantity)
            }
        }
    }

    override suspend fun deleteCartItem(cartId: Long) {
        cartItemDataSource.removeCartItem(cartId)
    }

    private suspend fun updateCartItemQuantity(
        cartId: Long,
        quantity: Int,
    ) {
        cartItemDataSource.updateCartItem(cartId, quantity)
    }
}
