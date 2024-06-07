package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.dto.ProductResponse

interface ProductDataSource {
    suspend fun getProductResponse(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<ProductResponse>

    suspend fun getProductById(productId: Int): Result<ProductDto>
}
