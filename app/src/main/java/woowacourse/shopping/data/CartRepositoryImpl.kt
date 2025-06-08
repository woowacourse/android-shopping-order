package woowacourse.shopping.data

import woowacourse.shopping.data.datasource.CartItemDataSource
import woowacourse.shopping.data.mapper.toCartItem
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.cartitem.CartItemContent
import woowacourse.shopping.data.model.response.Quantity
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.repository.CartRepository

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

    override suspend fun addCartItem(cartItem: CartItem) {
        val request = CartItemRequest(cartItem.product.id, cartItem.quantity)
        cartItemDataSource.submitCartItem(request)
    }

    override suspend fun deleteCartItem(cartId: Long) {
        cartItemDataSource.removeCartItem(cartId)
    }

    override suspend fun updateCartItemQuantity(
        cartId: Long,
        quantity: Int,
    ) {
        cartItemDataSource.updateCartItem(cartId, Quantity(quantity))
    }
}
