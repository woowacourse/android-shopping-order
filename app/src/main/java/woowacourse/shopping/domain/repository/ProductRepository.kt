package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.result.Result

interface ProductRepository {
    suspend fun allProductsResponse(
        page: Int,
        size: Int,
    ): Result<List<Product>>


    suspend fun productByIdResponse(id: Long): Result<Product>


    suspend fun allRecommendProductsResponse(category: String): Result<List<Product>>
}
