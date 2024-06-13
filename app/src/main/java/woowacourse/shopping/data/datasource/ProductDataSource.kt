package woowacourse.shopping.data.datasource

import woowacourse.shopping.remote.dto.ProductDto
import woowacourse.shopping.remote.dto.ProductResponse

interface ProductDataSource {
    suspend fun getProductResponse(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<ProductResponse>

    suspend fun getProductById(productId: Int): Result<ProductDto>
}
