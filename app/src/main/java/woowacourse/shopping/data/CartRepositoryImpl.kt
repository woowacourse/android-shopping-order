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
    override fun getCartItems(
        page: Int,
        limit: Int,
        callback: (List<CartItem>, Boolean) -> Unit,
    ) {
        cartItemDataSource.fetchCartItems(page, limit) { response ->
            if (response != null) {
                val cartItems = response.content.map { it.toCartItem() }
                callback(cartItems, !response.last)
            }
        }
    }

    override fun getAllCartItems(callback: (List<CartItem>?) -> Unit) {
        cartItemDataSource.fetchCartItems(
            page = 0,
            size = Int.MAX_VALUE,
        ) { it ->
            callback(it?.content?.map { it.toCartItem() }.orEmpty())
        }
    }

    override fun getAllCartItemsCount(callBack: (Quantity?) -> Unit) {
        cartItemDataSource.fetchCartItemsCount {
            callBack(it)
        }
    }

    override fun deleteCartItem(
        id: Long,
        callback: (Long) -> Unit,
    ) {
        cartItemDataSource.removeCartItem(id) {
            callback(id)
        }
    }

    override fun upsertCartItemQuantity(
        productId: Long,
        cartId: Long?,
        quantity: Int,
        callback: (Long) -> Unit,
    ) {
        if (cartId != null) {
            cartItemDataSource.updateCartItem(cartId, Quantity(quantity)) {
                callback(cartId)
            }
        } else {
            val request = CartItemRequest(productId, quantity)
            cartItemDataSource.submitCartItem(request) { cartId ->
                callback(cartId)
            }
        }
    }

    override fun addCartItem(
        cartItem: CartItem,
        callback: (Long) -> Unit,
    ) {
        val item =
            CartItemRequest(
                productId = cartItem.product.id,
                quantity = cartItem.amount,
            )
        cartItemDataSource.submitCartItem(item) { cartId ->
            callback(cartId)
        }
    }

    override fun increaseCartItem(
        cartItem: CartItem,
        callback: (Long) -> Unit,
    ) {
        cartItemDataSource.updateCartItem(cartId = cartItem.cartId, quantity = Quantity(cartItem.amount + 1)) {
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

    override fun decreaseCartItem(
        cartItem: CartItem,
        callback: (Long) -> Unit,
    ) {
        cartItemDataSource.updateCartItem(cartId = cartItem.cartId, quantity = Quantity(cartItem.amount - 1)) {
            callback(it)
        }
    }
}
