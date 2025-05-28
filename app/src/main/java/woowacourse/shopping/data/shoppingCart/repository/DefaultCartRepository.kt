package woowacourse.shopping.data.shoppingCart.repository

import woowacourse.shopping.data.shoppingCart.PageableCartItemData
import woowacourse.shopping.data.shoppingCart.source.RemoteShoppingCartDataSource
import woowacourse.shopping.data.shoppingCart.source.ShoppingCartDataSource
import woowacourse.shopping.domain.cart.PageableCartItems
import kotlin.concurrent.thread

class DefaultCartRepository(
    private val shoppingCartDataSource: ShoppingCartDataSource = RemoteShoppingCartDataSource(),
) : CartRepository {
    override fun loadPageableCartItems(
        page: Int,
        size: Int,
        onLoad: (Result<PageableCartItems>) -> Unit,
    ) {
        {
            val pageableCartItemData: PageableCartItemData =
                shoppingCartDataSource.pageableCartItems(page, size)
            PageableCartItems(
                cartItems = pageableCartItemData.cartItems.map { it.toDomain() },
                hasPrevious = pageableCartItemData.hasPrevious,
                hasNext = pageableCartItemData.hasNext,
            )
        }.runAsync(onLoad)
    }

    override fun addCartItem(
        productId: Long,
        quantity: Int,
        onAdd: (Result<Unit>) -> Unit,
    ) {
        {
            shoppingCartDataSource.addCartItem(
                productId = productId,
                quantity = quantity,
            )
        }.runAsync(onAdd)
    }

    override fun remove(
        cartItemId: Long,
        onRemove: (Result<Unit>) -> Unit,
    ) {
        { shoppingCartDataSource.remove(cartItemId) }.runAsync(onRemove)
    }

    override fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
        onUpdate: (Result<Unit>) -> Unit,
    ) {
        {
            shoppingCartDataSource.updateCartItemQuantity(
                cartItemId = cartItemId,
                newQuantity = quantity,
            )
        }.runAsync(onUpdate)
    }

    override fun cartItemsSize(onResult: (Result<Int>) -> Unit) {
        { shoppingCartDataSource.cartItemsSize() }.runAsync(onResult)
    }

    private inline fun <T> (() -> T).runAsync(crossinline onResult: (Result<T>) -> Unit) {
        thread {
            val result = runCatching(this)
            onResult(result)
        }
    }
}
