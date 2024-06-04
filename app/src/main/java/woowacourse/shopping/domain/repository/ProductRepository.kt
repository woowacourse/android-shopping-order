package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.dto.ProductResponse

interface ProductRepository {
    fun getProductResponse(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<ProductResponse>

    fun getProductById(id: Int): Result<ProductDto>
}
