package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.response.ProductResponse
import woowacourse.shopping.data.model.response.ProductsResponse
import woowacourse.shopping.data.service.ProductService
import woowacourse.shopping.data.util.safeApiCall

class ProductDataSourceImpl(
    private val productService: ProductService,
) : ProductDataSource {
    override suspend fun fetchProduct(id: Long): Result<ProductResponse> =
        safeApiCall {
            productService.getProduct(id)
        }

    override suspend fun fetchProducts(
        page: Int,
        size: Int,
    ): Result<ProductsResponse> =
        safeApiCall {
            productService.getProducts(page = page, size = size)
        }
}
