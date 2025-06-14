package woowacourse.shopping.data.cart.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.cart.dto.CartResponse
import woowacourse.shopping.data.cart.source.CartDataSource
import woowacourse.shopping.di.DataSourceModule
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.cart.PagedCartItems
import woowacourse.shopping.domain.product.Product

class DefaultCartRepository(
    private val cartDataSource: CartDataSource = DataSourceModule.remoteCartDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CartRepository {
    override suspend fun loadPagedCartItems(
        page: Int,
        size: Int,
    ): PagedCartItems =
        withContext(ioDispatcher) {
            val cartResponse: CartResponse? =
                cartDataSource.pagedCartItems(page, size)

            PagedCartItems.from(
                cartItems =
                    cartResponse?.content?.mapNotNull { it.toDomainOrNull() }
                        ?: emptyList(),
                pageNumber = cartResponse?.pageable?.pageNumber,
                totalPages = cartResponse?.totalPages,
            )
        }

    override suspend fun loadCart(): List<CartItem> =
        withContext(ioDispatcher) {
            cartDataSource.cart().map { it.toDomain() }
        }

    override suspend fun addCartItem(
        productId: Long,
        quantity: Int,
    ) = withContext(ioDispatcher) {
        cartDataSource.addCartItem(
            productId = productId,
            quantity = quantity,
        )
    }

    override suspend fun remove(cartItemId: Long) =
        withContext(ioDispatcher) {
            cartDataSource.remove(cartItemId)
        }

    override suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ) = withContext(ioDispatcher) {
        cartDataSource.updateCartItemQuantity(
            cartItemId = cartItemId,
            newQuantity = quantity,
        )
    }

    override suspend fun cartItemsSize(): Int =
        withContext(ioDispatcher) {
            cartDataSource.cartItemsSize()
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
}
