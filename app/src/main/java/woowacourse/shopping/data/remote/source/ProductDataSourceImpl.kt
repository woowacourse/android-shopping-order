package woowacourse.shopping.data.remote.source

import woowacourse.shopping.data.remote.api.ApiClient
import woowacourse.shopping.data.remote.api.ProductApiService
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.data.source.ProductDataSource

class ProductDataSourceImpl(apiClient: ApiClient) : ProductDataSource {
    private val productApiService: ProductApiService =
        apiClient.createService(ProductApiService::class.java)

    override suspend fun loadProducts(
        page: Int,
        size: Int,
    ): Result<ProductResponse> {
        return Result.runCatching {
            productApiService.requestProducts(
                page = page,
                size = size,
            )
        }
    }

    override suspend fun loadCategoryProducts(
        page: Int,
        size: Int,
        category: String,
    ): Result<ProductResponse> {
        return Result.runCatching {
            productApiService.requestCategoryProducts(
                page = page,
                size = size,
                category = category,
            )
        }
    }

    override suspend fun loadProduct(id: Int): Result<ProductDto> {
        return Result.runCatching { productApiService.requestProduct(id = id) }
    }
}
