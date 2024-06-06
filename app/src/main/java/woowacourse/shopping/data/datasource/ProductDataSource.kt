package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.dto.ProductResponse

interface ProductDataSource {
    fun getProductResponse(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<ProductResponse>

    fun getProductById(productId: Int): Result<ProductDto>
}
