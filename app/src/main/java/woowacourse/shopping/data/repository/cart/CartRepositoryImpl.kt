package woowacourse.shopping.data.repository.cart

import woowacourse.shopping.data.network.cart.CartServerDao
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.domain.Product

class CartRepositoryImpl(
    private val cartServerDao: CartServerDao,
    private val productRepository: ProductRepository,
) : CartRepository {

    override suspend fun loadCart(): Cart = Cart(loadAll())

    override suspend fun loadTotalQuantity(): Int = cartServerDao.getTotalQuantity() ?: 0

    override suspend fun pagination(
        page: Int,
        pageSize: Int,
    ): List<CartContent> = cartServerDao.pagination(page, pageSize, emptyList())

    override suspend fun increase(product: Product) {
        val existing = loadAll().firstOrNull { it.hasProductId(product.id) }
        if (existing == null) {
            cartServerDao.insert(CartContent(product, 1))
        } else {
            cartServerDao.update(CartContent(existing.product, existing.quantity + 1))
        }
    }

    override suspend fun decrease(productId: String) {
        val existing = loadAll().firstOrNull { it.hasProductId(productId) } ?: return
        if (existing.quantity <= 1) {
            cartServerDao.deleteById(productId)
        } else {
            cartServerDao.update(CartContent(existing.product, existing.quantity - 1))
        }
    }

    override suspend fun remove(productId: String) {
        cartServerDao.deleteById(productId)
    }

    override suspend fun setProductQuantity(
        productId: String,
        quantity: Int,
    ) {
        if (quantity < 1) return
        val existing = loadAll().firstOrNull { it.hasProductId(productId) }
        if (existing != null) {
            cartServerDao.update(CartContent(existing.product, quantity))
            return
        }
        val product = productRepository.getProduct(productId)
        cartServerDao.insert(CartContent(product, quantity))
    }

    private suspend fun loadAll(): List<CartContent> = cartServerDao.pagination(0, ALL_PAGE_SIZE, emptyList())

    companion object {
        private const val ALL_PAGE_SIZE = 1000
    }
}
