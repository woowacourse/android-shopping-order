package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.result.Result

interface ProductRepository {
    suspend fun getAllProducts(
        page: Int,
        size: Int,
    ): Result<List<Product>>


    suspend fun getProductById(id: Long): Result<Product>


    suspend fun getAllRecommendProducts(category: String): Result<List<Product>>
}
