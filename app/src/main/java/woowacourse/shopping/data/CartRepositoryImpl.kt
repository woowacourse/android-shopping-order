package woowacourse.shopping.data

import woowacourse.shopping.data.datasource.CartItemDataSource
import woowacourse.shopping.data.mapper.toCartItem
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.Quantity
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource,
) : CartRepository {
    override fun loadPageOfCartItems(
        pageIndex: Int,
        pageSize: Int,
        callback: (List<CartItem>, Boolean) -> Unit,
    ) {
        cartItemDataSource.fetchPageOfCartItems(pageIndex, pageSize) { response ->
            if (response != null) {
                val cartItems = response.content.map { it.toCartItem() }
                callback(cartItems, !response.last)
            }
        }
    }

    override fun loadAllCartItems(callback: (List<CartItem>) -> Unit) {
        cartItemDataSource.fetchPageOfCartItems(
            pageIndex = 0,
            pageSize = Int.MAX_VALUE,
        ) { response ->
            callback(response?.content?.map { content -> content.toCartItem() }.orEmpty())
        }
    }

    override fun getAllCartItemsCount(callBack: (Int) -> Unit) {
        cartItemDataSource.fetchCartItemsCount { quantity ->
            callBack(quantity?.quantity ?: 0)
        }
    }

    override fun deleteCartItem(
        cartId: Long,
        callback: (Long) -> Unit,
    ) {
        cartItemDataSource.removeCartItem(cartId) {
            callback(cartId)
        }
    }

    override fun addCartItem(
        cartItem: CartItem,
        callback: () -> Unit,
    ) {
        val item =
            CartItemRequest(
                productId = cartItem.product.id,
                quantity = cartItem.quantity,
            )

        cartItemDataSource.submitCartItem(item) {
            callback()
        }
    }

    override fun increaseQuantity(
        cartItem: CartItem,
        callback: (Long) -> Unit,
    ) {
        cartItemDataSource.updateCartItem(
            cartId = cartItem.cartId,
            quantity = Quantity(cartItem.quantity + 1),
        ) {
            callback(it)
        }
    }

    override fun decreaseQuantity(
        cartItem: CartItem,
        callback: (Long) -> Unit,
    ) {
        cartItemDataSource.updateCartItem(
            cartId = cartItem.cartId,
            quantity = Quantity(cartItem.quantity - 1),
        ) {
            callback(it)
        }
    }

    override fun updateCartItemQuantity(
        cartId: Long,
        quantity: Int,
        callback: (Long) -> Unit,
    ) {
        cartItemDataSource.updateCartItem(cartId, Quantity(quantity)) {
            callback(cartId)
        }
    }
}
