package woowacourse.shopping.data.remote.source

import retrofit2.Call
import woowacourse.shopping.data.remote.api.ApiClient
import woowacourse.shopping.data.remote.api.ProductApiService
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.data.source.ProductDataSource

class ProductDataSourceImpl(apiClient: ApiClient) : ProductDataSource {
    private val productApiService: ProductApiService =
        apiClient.createService(ProductApiService::class.java)

    override fun loadProducts(
        page: Int,
        size: Int,
    ): Call<ProductResponse> {
        return productApiService.requestProducts(
            page = page,
            size = size,
        )
    }

    override fun loadCategoryProducts(
        page: Int,
        size: Int,
        category: String,
    ): Call<ProductResponse> {
        return productApiService.requestCategoryProducts(
            page = page,
            size = size,
            category = category,
        )
    }

    override fun loadProduct(id: Int): Call<ProductDto> {
        return productApiService.requestProduct(id = id)
    }
}
