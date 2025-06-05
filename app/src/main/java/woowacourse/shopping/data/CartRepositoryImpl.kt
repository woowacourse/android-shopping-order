package woowacourse.shopping.data

import woowacourse.shopping.data.datasource.CartItemDataSource
import woowacourse.shopping.data.mapper.toCartItem
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemContent
import woowacourse.shopping.data.model.response.Quantity
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource,
) : CartRepository {
    override fun loadPageOfCartItems(
        pageIndex: Int,
        pageSize: Int,
        callback: (
            cartItems: List<CartItem>,
            isFirstPage: Boolean,
            isLastPage: Boolean,
        ) -> Unit,
    ) {
        cartItemDataSource.fetchPageOfCartItems(pageIndex, pageSize) { response ->
            if (response != null) {
                val cartItems = response.content.map(CartItemContent::toCartItem)
                callback(cartItems, response.first, response.last)
            }
        }
    }

    override fun loadAllCartItems(callback: (cartItems: List<CartItem>) -> Unit) {
        cartItemDataSource.fetchPageOfCartItems(
            pageIndex = 0,
            pageSize = Int.MAX_VALUE,
        ) { response ->
            callback(response?.content?.map(CartItemContent::toCartItem).orEmpty())
        }
    }

    override fun findCartItemByProductId(
        id: Long,
        callback: (cartItem: CartItem?) -> Unit,
    ) {
        cartItemDataSource.fetchPageOfCartItems(0, Int.MAX_VALUE) { response ->
            callback(response?.content?.find { it.product.id == id }?.toCartItem())
        }
    }

    override fun getAllCartItemsCount(callback: (totalCount: Int) -> Unit) {
        cartItemDataSource.fetchCartItemsCount { quantity ->
            callback(quantity?.quantity ?: 0)
        }
    }

    override fun increaseQuantity(
        cartItem: CartItem,
        callback: () -> Unit,
    ) {
        cartItemDataSource.updateCartItem(
            cartId = cartItem.cartId,
            quantity = Quantity(cartItem.quantity + 1),
        ) {
            callback()
        }
    }

    override fun decreaseQuantity(
        cartItem: CartItem,
        callback: () -> Unit,
    ) {
        cartItemDataSource.updateCartItem(
            cartId = cartItem.cartId,
            quantity = Quantity(cartItem.quantity - 1),
        ) {
            callback()
        }
    }

    override fun addCartItem(
        cartItem: CartItem,
        callback: (addedCartItem: CartItem?) -> Unit,
    ) {
        val item = CartItemRequest(cartItem.product.id, cartItem.quantity)
        cartItemDataSource.submitCartItem(item) {
            findCartItemByProductId(cartItem.product.id) { addedCartItem ->
                callback(addedCartItem)
            }
        }
    }

    override fun deleteCartItem(
        cartId: Long,
        callback: () -> Unit,
    ) {
        cartItemDataSource.removeCartItem(cartId) {
            callback()
        }
    }

    override fun updateCartItemQuantity(
        cartId: Long,
        quantity: Int,
        callback: () -> Unit,
    ) {
        cartItemDataSource.updateCartItem(cartId, Quantity(quantity)) {
            callback()
        }
    }
}
