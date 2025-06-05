package woowacourse.shopping.data.cart.repository

import woowacourse.shopping.data.cart.dto.CartResponse
import woowacourse.shopping.data.cart.source.CartDataSource
import woowacourse.shopping.di.DataSourceModule
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.cart.PagedCartItems
import woowacourse.shopping.domain.product.Product
import kotlin.concurrent.thread

class DefaultCartRepository(
    private val cartDataSource: CartDataSource = DataSourceModule.remoteCartDataSource,
) : CartRepository {
    override fun loadPagedCartItems(
        page: Int,
        size: Int,
        onLoad: (Result<PagedCartItems>) -> Unit,
    ) {
        {
            val cartResponse: CartResponse? =
                cartDataSource.pagedCartItems(page, size)

            PagedCartItems.from(
                cartItems =
                    cartResponse?.content?.mapNotNull { it.toDomainOrNull() }
                        ?: emptyList(),
                pageNumber = cartResponse?.pageable?.pageNumber,
                totalPages = cartResponse?.totalPages,
            )
        }.runAsync(onLoad)
    }

    override fun loadCart(onLoad: (Result<List<CartItem>>) -> Unit) {
        { cartDataSource.cart().map { it.toDomain() } }.runAsync(onLoad)
    }

    override fun addCartItem(
        productId: Long,
        quantity: Int,
        onAdd: (Result<Unit>) -> Unit,
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

    private fun CartResponse.Content.toDomainOrNull(): CartItem? =
        if (id == null ||
            product?.id == null ||
            product.name == null ||
            product.price == null ||
            product.category == null ||
            product.imageUrl == null ||
            quantity == null
        ) {
            null
        } else {
            CartItem(
                id = id,
                product =
                    Product(
                        id = product.id,
                        name = product.name,
                        price = product.price,
                        category = product.category,
                        imageUrl = product.imageUrl,
                    ),
                quantity = quantity,
            )
        }

    private inline fun <T> (() -> T).runAsync(crossinline onResult: (Result<T>) -> Unit) {
        thread {
            val result = runCatching(this)
            onResult(result)
        }
    }
}
