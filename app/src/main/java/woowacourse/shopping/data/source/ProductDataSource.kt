package woowacourse.shopping.data.source

import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse

interface ProductDataSource {
    suspend fun loadProducts(
        page: Int,
        size: Int,
    ): Result<ProductResponse>

    suspend fun loadCategoryProducts(
        page: Int,
        size: Int,
        category: String,
    ): Result<ProductResponse>

    suspend fun loadProduct(id: Int): Result<ProductDto>
}
