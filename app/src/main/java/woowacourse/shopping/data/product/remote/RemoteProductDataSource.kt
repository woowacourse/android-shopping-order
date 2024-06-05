package woowacourse.shopping.data.product.remote

import retrofit2.Call
import woowacourse.shopping.data.dto.response.ProductDto
import woowacourse.shopping.data.dto.response.ProductResponse
import woowacourse.shopping.data.remote.ApiClient

class RemoteProductDataSource {
    private val productApiService: ProductApiService =
        ApiClient.getApiClient().create(ProductApiService::class.java)

    fun loadById(productId: Long): Call<ProductDto> {
        return productApiService.requestProductDetail(productId = productId.toInt())
    }

    fun load(
        startPage: Int,
        pageSize: Int,
    ): Call<ProductResponse> {
        return productApiService.requestProducts(page = startPage, size = pageSize)
    }

    fun loadWithCategory(
        category: String,
        startPage: Int,
        pageSize: Int,
    ): Call<ProductResponse> {
        return productApiService.requestProductsWithCategory(category = category, page = startPage, size = pageSize)
    }
}
