package woowacourse.shopping.data.product.remote.datasource

import woowacourse.shopping.data.common.ApiResponseHandler.handleApiResponse
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.product.remote.ProductsApiService
import woowacourse.shopping.data.product.remote.dto.ProductDto
import woowacourse.shopping.data.product.remote.dto.ProductResponse

class ProductRemoteDataSource(
    private val productsApiService: ProductsApiService,
) : ProductDataSource {
    override suspend fun loadByPaged(page: Int): ResponseResult<ProductResponse> =
        handleApiResponse { productsApiService.requestProducts(page = page) }

    override suspend fun loadById(id: Long): ResponseResult<ProductDto> =
        handleApiResponse { productsApiService.requestProduct(id.toInt()) }

    override suspend fun loadByCategory(category: String): ResponseResult<ProductResponse> =
        handleApiResponse { productsApiService.requestProducts(category) }

    companion object {
        private const val TAG = "ProductRemoteDataSource"
    }
}
