package woowacourse.shopping.data.product

import woowacourse.shopping.data.ResponseResult
import woowacourse.shopping.data.handleResponseResult
import woowacourse.shopping.remote.product.ProductDto
import woowacourse.shopping.remote.product.ProductResponse
import woowacourse.shopping.remote.product.ProductsApiService

class ProductRemoteDataSource(
    private val productsApiService: ProductsApiService,
) : ProductDataSource {

    override fun findByPaged(page: Int): ResponseResult<ProductResponse> = handleResponseResult { productsApiService.requestProducts(page = page).execute() }

    override fun findById(id: Long): ResponseResult<ProductDto> = handleResponseResult { productsApiService.requestProduct(id.toInt()).execute() }

    override fun findByCategory(category: String): ResponseResult<ProductResponse> = handleResponseResult { productsApiService.requestProducts(category).execute() }

    override fun shutDown(): Boolean {
        // TODO: 연결 끊기
        return false
    }

    companion object {
        private const val TAG = "ProductRemoteDataSource"
    }
}
