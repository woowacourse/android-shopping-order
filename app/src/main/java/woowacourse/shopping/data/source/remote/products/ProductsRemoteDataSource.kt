package woowacourse.shopping.data.source.remote.products

import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.model.ProductsResponse
import woowacourse.shopping.data.source.remote.api.ProductsApiService

class ProductsRemoteDataSource(
    private val api: ProductsApiService,
) : ProductsDataSource {
    override suspend fun getProducts(
        page: Int?,
        size: Int?,
    ): Result<ProductsResponse> {
        val response = api.getProducts(page = page, size = size)
        return if (response.isSuccessful) {
            Result.success(response.body() ?: ProductsResponse.EMPTY)
        } else {
            Result.failure(RuntimeException("Error: ${response.code()}"))
        }
    }

    override suspend fun getProductById(id: Long): Result<ProductResponse> {
        val response = api.getProductById(id = id)
        return if (response.isSuccessful) {
            Result.success(response.body() ?: ProductResponse.EMPTY)
        } else {
            Result.failure(RuntimeException("Error: ${response.code()}"))
        }
    }

    override suspend fun getProductsByCategory(category: String): Result<ProductsResponse> {
        val response = api.getProductsByCategory(category = category)
        return if (response.isSuccessful) {
            Result.success(response.body() ?: ProductsResponse.EMPTY)
        } else {
            Result.failure(RuntimeException("Error: ${response.code()}"))
        }
    }
}
