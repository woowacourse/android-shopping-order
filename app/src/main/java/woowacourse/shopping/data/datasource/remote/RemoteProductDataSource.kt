package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.remote.NetworkResult
import woowacourse.shopping.remote.dto.response.ProductDto
import woowacourse.shopping.remote.dto.response.ProductResponse

interface RemoteProductDataSource {
    suspend fun getProductById(productId: Long): NetworkResult<ProductDto>

    suspend fun getProducts(
        startPage: Int,
        pageSize: Int,
    ): NetworkResult<ProductResponse>

    suspend fun getProductsByCategory(
        category: String,
        startPage: Int,
        pageSize: Int,
    ): NetworkResult<ProductResponse>
}
