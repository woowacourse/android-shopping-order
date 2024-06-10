package woowacourse.shopping.data.product.remote

import woowacourse.shopping.data.dto.response.ProductItemResponse
import woowacourse.shopping.data.dto.response.ProductResponse
import woowacourse.shopping.data.remote.ApiClient

class RemoteProductDataSource {
    private val productApiService: ProductApiService =
        ApiClient.getApiClient().create(ProductApiService::class.java)

    suspend fun loadById(productId: Long): ProductItemResponse {
        return productApiService.requestProductDetail(productId = productId.toInt())
    }

    suspend fun load(
        startPage: Int,
        pageSize: Int,
    ): ProductResponse {
        return productApiService.requestProducts(page = startPage, size = pageSize)
    }

    suspend fun loadWithCategory(
        category: String,
        startPage: Int,
        pageSize: Int,
    ): ProductResponse {
        return productApiService.requestProductsWithCategory(
            category = category,
            page = startPage,
            size = pageSize,
        )
    }
}
