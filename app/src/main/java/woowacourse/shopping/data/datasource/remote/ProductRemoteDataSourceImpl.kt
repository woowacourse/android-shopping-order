package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.model.common.PageableResponse
import woowacourse.shopping.data.model.product.ProductResponse
import woowacourse.shopping.data.service.ProductService
import woowacourse.shopping.data.util.safeApiCall

class ProductRemoteDataSourceImpl(
    private val productService: ProductService,
) : ProductRemoteDataSource {
    override suspend fun fetchProducts(
        category: String?,
        page: Int,
        size: Int,
    ): Result<PageableResponse<ProductResponse>> =
        safeApiCall {
            productService.fetchProducts(category, page, size)
        }

    override suspend fun fetchProduct(productId: Long): Result<ProductResponse> = safeApiCall { productService.fetchProduct(productId) }
}
