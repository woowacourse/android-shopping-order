package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartItemDataSource
import woowacourse.shopping.data.mapper.toCartItem
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.Quantity
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.PagedCartItems
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource,
) : CartRepository {
    override suspend fun getCartItems(
        page: Int,
        limit: Int,
    ): Result<PagedCartItems> =
        runCatching {
            val response = cartItemDataSource.fetchCartItems(page, limit).getOrThrow()
            val cartItems = response.content.map { it.toCartItem() }
            val hasMore = !response.last

            PagedCartItems(cartItems, hasMore)
        }

    override suspend fun getAllCartItems(): Result<List<CartItem>> =
        runCatching {
            val response = cartItemDataSource.fetchCartItems(0, Int.MAX_VALUE).getOrThrow()
            response.content.map { it.toCartItem() }
        }

    override suspend fun getAllCartItemsCount(): Result<Quantity> =
        runCatching {
            cartItemDataSource.fetchCartItemsCount().getOrThrow()
        }

    override suspend fun deleteCartItem(productId: Long): Result<Long> =
        runCatching {
            cartItemDataSource.removeCartItem(productId).getOrThrow()
            productId
        }

    override suspend fun upsertCartItemQuantity(
        productId: Long,
        cartId: Long?,
        quantity: Int,
    ): Result<Long> =
        runCatching {
            if (cartId != null) {
                cartItemDataSource.updateCartItem(cartId, Quantity(quantity)).getOrThrow()
                cartId
            } else {
                val request = CartItemRequest(productId, quantity)
                cartItemDataSource.submitCartItem(request).getOrThrow()
            }
        }

    override suspend fun addOrIncreaseCartItem(productId: Long): Result<Long> =
        runCatching {
            val allItems = getAllCartItems().getOrThrow()
            val existing = allItems.find { it.product.id == productId }

            if (existing != null) {
                increaseCartItem(existing).getOrThrow()
            } else {
                val request = CartItemRequest(productId, 1)
                cartItemDataSource.submitCartItem(request).getOrThrow()
            }
        }

    override suspend fun addCartItem(cartItem: CartItem): Result<Long> =
        runCatching {
            val request = CartItemRequest(cartItem.product.id, cartItem.amount)
            cartItemDataSource.submitCartItem(request).getOrThrow()
        }

    override suspend fun increaseCartItem(cartItem: CartItem): Result<Long> =
        runCatching {
            cartItemDataSource
                .updateCartItem(
                    cartId = cartItem.cartId,
                    quantity = Quantity(cartItem.amount + 1),
                ).getOrThrow()
            cartItem.cartId
        }

    override suspend fun decreaseCartItem(cartItem: CartItem): Result<Long> =
        runCatching {
            cartItemDataSource
                .updateCartItem(
                    cartId = cartItem.cartId,
                    quantity = Quantity(cartItem.amount - 1),
                ).getOrThrow()
            cartItem.cartId
        }
}
