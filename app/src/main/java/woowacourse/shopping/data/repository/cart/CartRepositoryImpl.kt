package woowacourse.shopping.data.repository.cart

import woowacourse.shopping.data.source.cart.CartServerDataSource
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.domain.Product

class CartRepositoryImpl(
    private val cartServerDataSource: CartServerDataSource,
) : CartRepository {
    override suspend fun loadCart(): Cart = Cart(loadAll())

    override suspend fun loadTotalQuantity(): Int =
        cartServerDataSource.getTotalQuantity()
            ?: 0

    override suspend fun pagination(
        page: Int,
        pageSize: Int,
    ): List<CartContent> = cartServerDataSource.pagination(page, pageSize, emptyList())

    override suspend fun increase(
        product: Product,
        quantity: Int,
    ) {
        val existing = loadAll().firstOrNull { it.hasProductId(product.id) }
        if (existing == null) {
            cartServerDataSource.insertCartItem(CartContent(product, quantity))
        } else {
            cartServerDataSource.updateCartItem(
                CartContent(existing.product, existing.quantity + quantity, existing.id),
            )
        }
    }

    override suspend fun decrease(productId: Long) {
        val existing =
            loadAll().firstOrNull { it.hasProductId(productId) }
                ?: return
        if (CartContent.isGreaterThanZero(existing.quantity)) {
            cartServerDataSource.updateCartItem(
                CartContent(existing.product, existing.quantity - 1, existing.id),
            )
        } else {
            cartServerDataSource.deleteById(existing.id)
        }
    }

    override suspend fun remove(contentId: Long) {
        val existing =
            loadAll().firstOrNull { it.id == contentId }
                ?: return
        cartServerDataSource.deleteById(existing.id)
    }

    override suspend fun setProductQuantity(
        product: Product,
        quantity: Int,
    ) {
        if (quantity < 1) return
        val existing = loadAll().firstOrNull { it.hasProductId(product.id) }
        if (existing != null) {
            cartServerDataSource.updateCartItem(CartContent(existing.product, quantity, existing.id))
            return
        }
        cartServerDataSource.insertCartItem(CartContent(product, quantity))
    }

    private suspend fun loadAll(): List<CartContent> = cartServerDataSource.pagination(0, ALL_PAGE_SIZE, emptyList())

    companion object {
        private const val ALL_PAGE_SIZE = 1000
    }
}
