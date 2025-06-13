package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Product

class FakeProductRepository : ProductRepository {
    private val products = mutableListOf<Product>()
    private val cartItems = mutableListOf<CartItem>()
    private val recentProductIds = mutableListOf<Long>()

    override fun loadProducts(
        lastItemId: Long,
        loadSize: Int,
        callback: (List<Product>, Boolean) -> Unit,
    ) {
        val result = products.filter { it.id > lastItemId }.take(loadSize)
        val lastId = result.lastOrNull()?.id ?: return callback(result, false)
        val hasMore = products.any { it.id > lastId }
        callback(result, hasMore)
    }

    override fun loadAllCartItems(callback: (List<CartItem>?) -> Unit) {
        callback(cartItems)
    }

    override fun addRecentProduct(product: Product) {
        recentProductIds.remove(product.id)
        recentProductIds.add(0, product.id)
    }

    override fun loadRecentProducts(
        count: Int,
        callback: (List<Product>) -> Unit,
    ) {
        val recentProducts =
            recentProductIds
                .take(count)
                .mapNotNull { id -> products.find { it.id == id } }
        callback(recentProducts)
    }

    override fun loadLastViewedProduct(
        currentProductId: Long,
        callback: (Product?) -> Unit,
    ) {
        val lastViewedId =
            recentProductIds
                .firstOrNull { it != currentProductId }
        val product = products.find { it.id == lastViewedId }
        callback(product)
    }

    override fun findProductById(
        id: Long,
        callback: (Product?) -> Unit,
    ) {
        val product = products.find { it.id == id }
        callback(product)
    }
}
