package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartItemDataSource
import woowacourse.shopping.data.mapper.toCartItem
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.Quantity
import woowacourse.shopping.data.model.response.cartitem.CartItemContent
import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Page

class CartRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource,
) : CartRepository {
    override suspend fun loadCartItemByProductId(id: Long): CartItem? {
        val response = cartItemDataSource.fetchPageOfCartItems(0, Int.MAX_VALUE)
        return response.content.find { content -> content.product.id == id }?.toCartItem()
    }

    override suspend fun loadPageOfCartItems(
        pageIndex: Int,
        pageSize: Int,
    ): Page<CartItem> {
        val response = cartItemDataSource.fetchPageOfCartItems(pageIndex, pageSize)
        return Page(
            response.content.map(CartItemContent::toCartItem),
            response.first,
            response.last,
        )
    }

    override suspend fun loadAllCartItems(): List<CartItem> {
        val response = cartItemDataSource.fetchPageOfCartItems(0, Int.MAX_VALUE)
        return response.content.map(CartItemContent::toCartItem)
    }

    override suspend fun loadTotalCartCount(): Int {
        return cartItemDataSource.fetchCartItemsCount().quantity
    }

    override suspend fun increaseQuantity(cartItem: CartItem) {
        cartItemDataSource.updateCartItem(
            cartId = cartItem.cartId,
            quantity = Quantity(cartItem.quantity + 1),
        )
    }

    override suspend fun decreaseQuantity(cartItem: CartItem) {
        cartItemDataSource.updateCartItem(
            cartId = cartItem.cartId,
            quantity = Quantity(cartItem.quantity - 1),
        )
    }

    override suspend fun addCartItem(cartItem: CartItem): CartItem? {
        val request = CartItemRequest(cartItem.product.id, cartItem.quantity)
        cartItemDataSource.submitCartItem(request)
        return loadCartItemByProductId(cartItem.product.id)
    }

    override suspend fun deleteCartItem(cartId: Long) {
        cartItemDataSource.removeCartItem(cartId)
    }

    override suspend fun deleteCartItems(cartIds: List<Long>) {
        cartItemDataSource.removeCartItems(cartIds)
    }

    override suspend fun updateCartItemQuantity(
        cartId: Long,
        quantity: Int,
    ) {
        cartItemDataSource.updateCartItem(cartId, Quantity(quantity))
    }
}
