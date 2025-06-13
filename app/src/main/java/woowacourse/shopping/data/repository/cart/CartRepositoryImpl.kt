package woowacourse.shopping.data.repository.cart

import woowacourse.shopping.data.datasource.cart.CartDataSource
import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Page

class CartRepositoryImpl(
    private val cartDataSource: CartDataSource,
) : CartRepository {
    override suspend fun loadCartItemByProductId(id: Long): CartItem? {
        val cartItems = cartDataSource.fetchPageOfCartItems(0, Int.MAX_VALUE).items
        return cartItems.find { cartItem -> cartItem.product.id == id }
    }

    override suspend fun loadPageOfCartItems(
        pageIndex: Int,
        pageSize: Int,
    ): Page<CartItem> {
        return cartDataSource.fetchPageOfCartItems(pageIndex, pageSize)
    }

    override suspend fun loadAllCartItems(): List<CartItem> {
        return cartDataSource.fetchPageOfCartItems(0, Int.MAX_VALUE).items
    }

    override suspend fun loadTotalCartCount(): Int {
        return cartDataSource.fetchCartItemsCount()
    }

    override suspend fun increaseQuantity(cartItem: CartItem) {
        cartDataSource.updateCartItem(
            cartItem.cartId,
            cartItem.quantity + 1,
        )
    }

    override suspend fun decreaseQuantity(cartItem: CartItem) {
        cartDataSource.updateCartItem(
            cartItem.cartId,
            cartItem.quantity - 1,
        )
    }

    override suspend fun addCartItem(cartItem: CartItem): CartItem? {
        cartDataSource.submitCartItem(cartItem.product.id, cartItem.quantity)
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
        cartDataSource.removeCartItem(cartId)
    }

    private suspend fun updateCartItemQuantity(
        cartId: Long,
        quantity: Int,
    ) {
        cartDataSource.updateCartItem(cartId, quantity)
    }
}
