package woowacourse.shopping.data.cart.remote

import woowacourse.shopping.data.remote.RetrofitClient.cartItemApi
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository

object RemoteCartRepository : CartRepository {
    private const val MAX_CART_ITEM_COUNT = 9999999

    override suspend fun find(cartItemId: Int): Result<CartItem?> {
        return cartItemApi.requestCartItems(page = 0, size = MAX_CART_ITEM_COUNT)
            .map { it.toCartItems().find { cartItem -> cartItem.id == cartItemId } }
    }

    override suspend fun findByProductId(productId: Int): Result<CartItem?> {
        return cartItemApi.requestCartItems(page = 0, size = MAX_CART_ITEM_COUNT)
            .map { it.toCartItems().find { cartItem -> cartItem.product.id == productId } }
    }

    override suspend fun findAll(): Result<List<CartItem>> {
        return cartItemApi.requestCartItems(page = 0, size = MAX_CART_ITEM_COUNT)
            .map { it.toCartItems() }
    }

    override suspend fun delete(id: Int): Result<Unit> {
        return cartItemApi.deleteCartItem(id = id)
    }

    override suspend fun add(
        productId: Int,
        quantity: Quantity,
    ): Result<Unit> {
        return cartItemApi.addCartItem(
            addCartItemRequest = AddCartItemRequest(productId, quantity.count),
        )
    }

    override suspend fun changeQuantity(
        id: Int,
        quantity: Quantity,
    ): Result<Unit> {
        return cartItemApi.setCartItemQuantity(
            id = id,
            quantity = CartItemQuantityRequest(quantity.count),
        )
    }

    override suspend fun getTotalQuantity(): Result<Int> {
        return cartItemApi.requestCartQuantityCount().map { it.quantity }
    }
}
