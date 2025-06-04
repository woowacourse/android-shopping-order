package woowacourse.shopping.data.cart.repository

import woowacourse.shopping.data.cart.PageableCartItemData
import woowacourse.shopping.data.cart.source.CartDataSource
import woowacourse.shopping.data.cart.source.RemoteCartDataSource
import woowacourse.shopping.domain.Pageable
import woowacourse.shopping.domain.cart.CartItem
import kotlin.concurrent.thread

class DefaultCartRepository(
    private val cartDataSource: CartDataSource = RemoteCartDataSource(),
) : CartRepository {
    override fun loadPageableCartItems(
        page: Int,
        size: Int,
        onLoad: (Result<Pageable<CartItem>>) -> Unit,
    ) {
        {
            val pageableCartItemData: PageableCartItemData =
                cartDataSource.pageableCartItems(page, size)
            Pageable<CartItem>(
                items = pageableCartItemData.cartItems.map { it.toDomain() },
                hasPrevious = pageableCartItemData.hasPrevious,
                hasNext = pageableCartItemData.hasNext,
            )
        }.runAsync(onLoad)
    }

    override fun loadCart(onLoad: (Result<List<CartItem>>) -> Unit) {
        { cartDataSource.cart().map { it.toDomain() } }.runAsync(onLoad)
    }

    override fun addCartItem(
        productId: Long,
        quantity: Int,
        onAdd: (Result<Long?>) -> Unit,
    ) {
        {
            cartDataSource.addCartItem(
                productId = productId,
                quantity = quantity,
            )
        }.runAsync(onAdd)
    }

    override fun remove(
        cartItemId: Long,
        onRemove: (Result<Unit>) -> Unit,
    ) {
        { cartDataSource.remove(cartItemId) }.runAsync(onRemove)
    }

    override fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
        onUpdate: (Result<Unit>) -> Unit,
    ) {
        {
            cartDataSource.updateCartItemQuantity(
                cartItemId = cartItemId,
                newQuantity = quantity,
            )
        }.runAsync(onUpdate)
    }

    override fun cartItemsSize(onResult: (Result<Int>) -> Unit) {
        { cartDataSource.cartItemsSize() }.runAsync(onResult)
    }

    private inline fun <T> (() -> T).runAsync(crossinline onResult: (Result<T>) -> Unit) {
        thread {
            val result = runCatching(this)
            onResult(result)
        }
    }
}
