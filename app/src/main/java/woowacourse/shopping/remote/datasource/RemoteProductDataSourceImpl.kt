package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.RemoteProductDataSource
import woowacourse.shopping.remote.NetworkResult
import woowacourse.shopping.remote.api.ApiClient
import woowacourse.shopping.remote.api.ProductApiService
import woowacourse.shopping.remote.dto.response.ProductDto
import woowacourse.shopping.remote.dto.response.ProductResponse
import woowacourse.shopping.remote.executeSafeApiCall

class RemoteProductDataSourceImpl(
    private val productApiService: ProductApiService =
        ApiClient.getApiClient().create(
            ProductApiService::class.java,
        ),
) : RemoteProductDataSource {
    override suspend fun getProductById(productId: Long): NetworkResult<ProductDto> {
        return executeSafeApiCall { productApiService.requestProductDetail(productId = productId.toInt()) }
    }

    override suspend fun getProducts(
        startPage: Int,
        pageSize: Int,
    ): NetworkResult<ProductResponse> {
        return executeSafeApiCall { productApiService.requestProducts(startPage, pageSize) }
    }

    override suspend fun getProductsByCategory(
        category: String,
        startPage: Int,
        pageSize: Int,
    ): NetworkResult<ProductResponse> {
        return executeSafeApiCall {
            productApiService.requestProductsWithCategory(
                category = category,
                page = startPage,
                size = pageSize,
            )
        }
    }
}
