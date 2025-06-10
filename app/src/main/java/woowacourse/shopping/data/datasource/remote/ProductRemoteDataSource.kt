package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.product.PageableResponse
import woowacourse.shopping.data.dto.product.ProductContent

interface ProductRemoteDataSource {
    suspend fun fetchPagingProducts(
        page: Int? = null,
        pageSize: Int? = null,
        category: String? = null,
    ): Result<PageableResponse<ProductContent>>

    suspend fun fetchProductById(id: Long): Result<ProductContent>
}
