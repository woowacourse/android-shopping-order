package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun loadProducts(
        page: Int,
        loadSize: Int,
    ): Result<Pair<List<Product>, Boolean>>

    suspend fun getProductById(id: Long): Result<Product>

    suspend fun loadProductsByCategory(category: String): Result<List<Product>>

    suspend fun addRecentProduct(product: Product): Result<Product>

    suspend fun loadRecentProducts(limit: Int): Result<List<Product>>

    suspend fun loadLastViewedProduct(currentProductId: Long): Result<Product>

    suspend fun getMostRecentProduct(): Result<Product>
}
