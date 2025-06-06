package woowacourse.shopping.data.cart.repository

import woowacourse.shopping.data.cart.PageableCartItemData
import woowacourse.shopping.data.cart.source.CartDataSource
import woowacourse.shopping.data.cart.source.RemoteCartDataSource
import woowacourse.shopping.domain.Pageable
import woowacourse.shopping.domain.cart.CartItem

class DefaultCartRepository(
    private val cartDataSource: CartDataSource = RemoteCartDataSource(),
) : CartRepository {
    override suspend fun loadPageableCartItems(
        page: Int,
        size: Int,
    ): Result<Pageable<CartItem>> =
        runCatching {
            val pageableCartItemData: PageableCartItemData =
                cartDataSource.pageableCartItems(page, size)

            Pageable<CartItem>(
                items = pageableCartItemData.cartItems.map { it.toDomain() },
                hasPrevious = pageableCartItemData.hasPrevious,
                hasNext = pageableCartItemData.hasNext,
            )
        }

    override suspend fun loadCart(): Result<List<CartItem>> =
        runCatching {
            cartDataSource.cart().map { it.toDomain() }
        }

    override suspend fun addCartItem(
        productId: Long,
        quantity: Int,
    ): Result<Long?> =
        runCatching {
            cartDataSource.addCartItem(
                productId = productId,
                quantity = quantity,
            )
        }

    override suspend fun remove(cartItemId: Long): Result<Unit> =
        runCatching {
            cartDataSource.remove(cartItemId)
        }

    override suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            cartDataSource.updateCartItemQuantity(
                cartItemId = cartItemId,
                newQuantity = quantity,
            )
        }
}
