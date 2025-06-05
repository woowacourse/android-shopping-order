package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.model.common.PageableResponse
import woowacourse.shopping.data.model.product.ProductResponse

interface ProductRemoteDataSource {
    suspend fun fetchProducts(
        category: String?,
        page: Int,
        size: Int,
    ): Result<PageableResponse<ProductResponse>>

    suspend fun fetchProduct(productId: Long): Result<ProductResponse>
}
