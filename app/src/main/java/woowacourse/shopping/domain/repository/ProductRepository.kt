package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun loadProductsUpToPage(
        pageIndex: Int,
        pageSize: Int,
    ): Page<Product>

    suspend fun loadRecentProducts(count: Int): List<Product>

    suspend fun getMostRecentProduct(): Product?

    suspend fun loadProductsByCategory(category: String): List<Product>

    suspend fun loadRecommendedProducts(count: Int): List<Product>

    suspend fun loadAllCartItems(): List<CartItem>

    suspend fun findProductById(id: Long): Product?

    suspend fun addRecentProduct(product: Product)
}
