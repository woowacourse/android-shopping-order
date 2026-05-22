package woowacourse.shopping.data.repository.cart

import woowacourse.shopping.data.network.cart.CartServerDao
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.domain.Product

class CartRepositoryImpl(
    private val cartServerDao: CartServerDao,
) : CartRepository {
    override suspend fun loadCart(): Cart = Cart(loadAll())

    override suspend fun loadTotalQuantity(): Int =
        cartServerDao.getTotalQuantity()
            ?: 0

    override suspend fun pagination(
        page: Int,
        pageSize: Int,
    ): List<CartContent> = cartServerDao.pagination(page, pageSize, emptyList())

    override suspend fun increase(
        product: Product,
        quantity: Int,
    ) {
        val existing = loadAll().firstOrNull { it.hasProductId(product.id) }
        if (existing == null) {
            cartServerDao.insertCartItem(CartContent(product, quantity))
        } else {
            cartServerDao.updateCartItem(
                CartContent(existing.product, existing.quantity + quantity, existing.id),
            )
        }
    }

    override suspend fun decrease(productId: Long) {
        val existing =
            loadAll().firstOrNull { it.hasProductId(productId) }
                ?: return
        if (existing.quantity <= 1) {
            cartServerDao.deleteById(existing.id)
        } else {
            cartServerDao.updateCartItem(
                CartContent(existing.product, existing.quantity - 1, existing.id),
            )
        }
    }

    override suspend fun remove(contentId: Long) {
        val existing =
            loadAll().firstOrNull { it.hasProductId(contentId) }
                ?: return
        cartServerDao.deleteById(existing.id)
    }

    override suspend fun setProductQuantity(
        product: Product,
        quantity: Int,
    ) {
        if (quantity < 1) return
        val existing = loadAll().firstOrNull { it.hasProductId(product.id) }
        if (existing != null) {
            cartServerDao.updateCartItem(CartContent(existing.product, quantity, existing.id))
            return
        }
        cartServerDao.insertCartItem(CartContent(product, quantity))
    }

    private suspend fun loadAll(): List<CartContent> = cartServerDao.pagination(0, ALL_PAGE_SIZE, emptyList())

    companion object {
        private const val ALL_PAGE_SIZE = 1000
    }
}
