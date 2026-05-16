package woowacourse.shopping.data.repository.cart

import woowacourse.shopping.data.datasource.cart.CartDataSource
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartContent

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

    override suspend fun insert(
        productId: String,
        quantity: Int,
    ) {
        cartDataSource.insert(productId, quantity)
    }

    override suspend fun updateQuantity(
        contentId: String,
        quantity: Int,
    ) {
        cartDataSource.update(
            contentId, quantity,
        )
    }

    override suspend fun decrease(contentId: String) {
        val existing = loadAll().firstOrNull { it.id == contentId }
            ?: return
        if (existing.quantity <= 1) {
            cartDataSource.deleteById(existing.id)
        } else {
            cartDataSource.update(
                contentId = contentId,
                quantity = existing.quantity - 1,
            )
        }
    }

    override suspend fun remove(contentId: String) {
        cartDataSource.deleteById(contentId)
    }

    private suspend fun loadAll(): List<CartContent> = cartDataSource.pagination(0, ALL_PAGE_SIZE, emptyList())

    companion object {
        private const val ALL_PAGE_SIZE = 1000
    }
}
