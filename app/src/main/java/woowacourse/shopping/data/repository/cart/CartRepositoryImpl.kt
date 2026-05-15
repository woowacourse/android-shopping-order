package woowacourse.shopping.data.repository.cart

import woowacourse.shopping.data.datasource.cart.CartDataSource
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.domain.Product

class CartRepositoryImpl(
    private val cartDataSource: CartDataSource,
    private val productRepository: ProductRepository,
) : CartRepository {

    override suspend fun loadCart(): Cart = Cart(loadAll())

    override suspend fun loadTotalQuantity(): Int = cartDataSource.getTotalQuantity()
        ?: 0

    override suspend fun pagination(
        page: Int,
        pageSize: Int,
    ): List<CartContent> = cartDataSource.pagination(page, pageSize, emptyList())

    override suspend fun increase(
        product: Product,
        quantity: Int,
    ) {
        val existing = loadAll().firstOrNull { it.hasProductId(product.id) }
        if (existing == null) {
            cartDataSource.insert(CartContent(product, quantity))
        } else {
            cartDataSource.update(
                CartContent(existing.product, existing.quantity + 1, existing.id),
            )
        }
    }

    override suspend fun decrease(productId: String) {
        val existing = loadAll().firstOrNull { it.hasProductId(productId) }
            ?: return
        if (existing.quantity <= 1) {
            cartDataSource.deleteById(existing.id)
        } else {
            cartDataSource.update(
                CartContent(existing.product, existing.quantity - 1, existing.id),
            )
        }
    }

    override suspend fun remove(productId: String) {
        val existing = loadAll().firstOrNull { it.hasProductId(productId) }
            ?: return
        cartDataSource.deleteById(existing.id)
    }

    override suspend fun setProductQuantity(
        productId: String,
        quantity: Int,
    ) {
        if (quantity < 1) return
        val existing = loadAll().firstOrNull { it.hasProductId(productId) }
        if (existing != null) {
            cartDataSource.update(CartContent(existing.product, quantity, existing.id))
            return
        }
        val product = productRepository.getProduct(productId)
        cartDataSource.insert(CartContent(product, quantity))
    }

    private suspend fun loadAll(): List<CartContent> = cartDataSource.pagination(0, ALL_PAGE_SIZE, emptyList())

    companion object {
        private const val ALL_PAGE_SIZE = 1000
    }
}
