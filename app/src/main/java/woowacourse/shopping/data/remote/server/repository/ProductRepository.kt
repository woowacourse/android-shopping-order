package woowacourse.shopping.data.remote.server.repository

import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.domain.Product

interface ProductRepository {
    suspend fun getProducts(
        page: Int,
        pageSize: Int,
    ): ApiResult<List<Product>>

    suspend fun getProduct(id: Long): ApiResult<Product>

    suspend fun getCategoryProducts(
        page: Int = 1,
        pageSize: Int = 10,
        category: String,
    ): ApiResult<List<Product>>
}
