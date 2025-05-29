package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.common.PageableResponse
import woowacourse.shopping.data.model.product.ProductResponse

interface ProductRemoteDataSource {
    fun fetchProducts(
        category: String?,
        page: Int?,
        size: Int?,
    ): Result<PageableResponse<ProductResponse>>

    fun fetchProduct(productId: Long): Result<ProductResponse>
}
