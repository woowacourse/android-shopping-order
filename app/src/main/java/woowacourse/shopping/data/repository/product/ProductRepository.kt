package woowacourse.shopping.data.repository.product

import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Page
import woowacourse.shopping.domain.Product

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

    suspend fun loadProductById(id: Long): Product?

    suspend fun addRecentProduct(product: Product)
}
