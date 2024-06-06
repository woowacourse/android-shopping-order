package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.repository.ApiResponse
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(
        page: Int,
        size: Int,
    ): ApiResponse<List<Product>>

    suspend fun find(id: Long): ApiResponse<Product>

    suspend fun productsByCategory(category: String): ApiResponse<List<Product>>
}
