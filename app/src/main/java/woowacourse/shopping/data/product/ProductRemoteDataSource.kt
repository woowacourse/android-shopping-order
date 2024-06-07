package woowacourse.shopping.data.product

import woowacourse.shopping.data.common.ResponseHandlingUtils.handleExecute
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.remote.product.ProductDto
import woowacourse.shopping.remote.product.ProductResponse
import woowacourse.shopping.remote.product.ProductsApiService

class ProductRemoteDataSource(
    private val productsApiService: ProductsApiService,
) : ProductDataSource {
    override suspend fun loadByPaged(page: Int): ResponseResult<ProductResponse> =
        handleExecute { productsApiService.requestProducts(page = page) }

    override suspend fun loadById(id: Long): ResponseResult<ProductDto> =
        handleExecute { productsApiService.requestProduct(id.toInt()) }

    override suspend fun loadByCategory(category: String): ResponseResult<ProductResponse> =
        handleExecute { productsApiService.requestProducts(category) }

    override fun shutDown(): Boolean {
        // TODO: 연결 끊기
        return false
    }

    companion object {
        private const val TAG = "ProductRemoteDataSource"
    }
}
