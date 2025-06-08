package woowacourse.shopping.data.source.remote.products

import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.model.ProductsResponse
import woowacourse.shopping.data.source.remote.api.ProductsApiService
import woowacourse.shopping.data.source.remote.util.safeApiCall

class ProductsRemoteDataSource(
    private val api: ProductsApiService,
) : ProductsDataSource {
    override suspend fun getProducts(
        page: Int?,
        size: Int?,
    ): Result<ProductsResponse> = safeApiCall {
        api.getProducts(page = page, size = size)
    }

    override suspend fun getProductById(id: Long): Result<ProductResponse> = safeApiCall {
        api.getProductById(id = id)
    }

    override suspend fun getProductsByCategory(category: String): Result<ProductsResponse> =
        safeApiCall {
            api.getProductsByCategory(category = category)
        }
}