package woowacourse.shopping.data.product

import woowacourse.shopping.model.Product

interface ProductRepository {
    suspend fun getProducts(
        page: Int,
        size: Int,
    ): Result<List<Product>>

    suspend fun find(id: Long): Result<Product>

    suspend fun getProductsByCategory(category: String): Result<List<Product>>
}
