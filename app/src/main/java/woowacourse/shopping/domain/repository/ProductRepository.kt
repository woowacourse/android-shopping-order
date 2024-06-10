package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.product.Product

interface ProductRepository {
    suspend fun getProducts(
        page: Int,
        size: Int,
    ): Result<List<Product>>

    suspend fun find(id: Long): Result<Product>

    suspend fun getProductsByCategory(category: String): Result<List<Product>>
}
