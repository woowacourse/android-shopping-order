package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.result.Result

interface ProductRepository {
    suspend fun allProducts(
        page: Int,
        size: Int,
    ): List<Product>

    suspend fun allProductsResponse(
        page: Int,
        size: Int,
    ): Result<List<Product>>

    suspend fun productById(id: Long): Product

    suspend fun productByIdOrNull(id: Long): Product?

    suspend fun productByIdResponse(id: Long): Result<Product>

    suspend fun allProductsByCategory(category: String): List<Product>

    suspend fun allProductsByCategoryResponse(category: String): Result<List<Product>>
}
