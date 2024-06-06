package woowacourse.shopping.data.product

import woowacourse.shopping.data.common.HandleResponseResult.handleResponseResult
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.remote.product.ProductDto
import woowacourse.shopping.remote.product.ProductResponse
import woowacourse.shopping.remote.product.ProductsApiService

class ProductRemoteDataSource(
    private val productsApiService: ProductsApiService,
) : ProductDataSource {
    override fun loadByPaged(page: Int): ResponseResult<ProductResponse> =
        handleResponseResult { productsApiService.requestProducts(page = page).execute() }

    override fun loadById(id: Long): ResponseResult<ProductDto> =
        handleResponseResult {
            productsApiService.requestProduct(id.toInt()).execute()
        }

    override fun loadByCategory(category: String): ResponseResult<ProductResponse> =
        handleResponseResult {
            productsApiService.requestProducts(category).execute()
        }

    override fun shutDown(): Boolean {
        // TODO: 연결 끊기
        return false
    }

    companion object {
        private const val TAG = "ProductRemoteDataSource"
    }
}
