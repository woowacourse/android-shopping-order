package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.result.Response

interface ProductRepository {
    suspend fun allProducts(
        page: Int,
        size: Int,
    ): List<Product>

    suspend fun allProductsResponse(
        page: Int,
        size: Int,
    ): Response<List<Product>>

    suspend fun productById(id:Long):Product

    suspend fun productByIdOrNull(id: Long): Product?

    suspend fun productByIdResponse(id:Long):Response<Product>

    suspend fun allProductsByCategory(category: String):List<Product>

    suspend fun allProductsByCategoryResponse(category: String): Response<List<Product>>
}
