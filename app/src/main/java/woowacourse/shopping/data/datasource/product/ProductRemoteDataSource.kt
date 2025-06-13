package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.data.di.ApiResult
import woowacourse.shopping.data.model.response.ProductDetailResponse
import woowacourse.shopping.data.model.response.ProductsResponse

interface ProductRemoteDataSource {
    suspend fun getProductDetail(productId: Long): ApiResult<ProductDetailResponse>

    suspend fun getProducts(
        category: String? = null,
        page: Int,
        size: Int,
    ): ApiResult<ProductsResponse>
}
