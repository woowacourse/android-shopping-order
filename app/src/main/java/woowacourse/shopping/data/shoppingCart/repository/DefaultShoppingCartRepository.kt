package woowacourse.shopping.data.shoppingCart.repository

import woowacourse.shopping.data.product.entity.CartItemEntity
import woowacourse.shopping.data.product.entity.CartItemEntity.Companion.toEntity
import woowacourse.shopping.data.shoppingCart.storage.RemoteShoppingCartDataSource
import woowacourse.shopping.data.shoppingCart.storage.ShoppingCartDataSource
import woowacourse.shopping.domain.cart.PageableCartItems
import woowacourse.shopping.domain.product.CartItem
import kotlin.concurrent.thread

class DefaultShoppingCartRepository(
    private val shoppingCartDataSource: ShoppingCartDataSource = RemoteShoppingCartDataSource(),
) : ShoppingCartRepository {
    override fun load(
        page: Int,
        size: Int,
        onLoad: (Result<PageableCartItems>) -> Unit,
    ) {
        {
            val response = shoppingCartDataSource.load(page, size)
            PageableCartItems(
                cartItems = response.cartItems.map(CartItemEntity::toDomain),
                hasPrevious = response.hasPrevious,
                hasNext = response.hasNext,
            )
        }.runAsync(onLoad)
    }

    override fun addCartItem(
        productId: Long,
        quantity: Int,
        onAdd: (Result<Unit>) -> Unit,
    ) {
        { shoppingCartDataSource.addCartItem(productId, quantity) }.runAsync(onAdd)
    }

    override fun remove(
        cartItem: CartItem,
        onRemove: (Result<Unit>) -> Unit,
    ) {
        { shoppingCartDataSource.remove(cartItem.toEntity()) }.runAsync(onRemove)
    }

    override fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
        onUpdate: (Result<Unit>) -> Unit,
    ) {
        { shoppingCartDataSource.updateCartItemQuantity(cartItemId, quantity) }.runAsync(onUpdate)
    }

    override fun quantity(onResult: (Result<Int>) -> Unit) {
        { shoppingCartDataSource.quantity() }.runAsync(onResult)
    }

    private inline fun <T> (() -> T).runAsync(crossinline onResult: (Result<T>) -> Unit) {
        thread {
            val result = runCatching(this)
            onResult(result)
        }
    }
}
