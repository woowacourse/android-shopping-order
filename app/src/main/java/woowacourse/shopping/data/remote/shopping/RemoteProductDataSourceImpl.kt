package woowacourse.shopping.data.remote.shopping

import retrofit2.Call
import woowacourse.shopping.data.remote.ApiClient
import woowacourse.shopping.data.remote.dto.response.ProductDto
import woowacourse.shopping.data.remote.dto.response.ProductResponse

class RemoteProductDataSourceImpl : RemoteProductDataSource {
    private val productApiService: ProductApiService =
        ApiClient.getApiClient().create(ProductApiService::class.java)

    override fun loadById(productId: Long): Call<ProductDto> {
        return productApiService.requestProductDetail(productId = productId.toInt())
    }

    override fun load(
        startPage: Int,
        pageSize: Int,
    ): Call<ProductResponse> {
        return productApiService.requestProducts(page = startPage, size = pageSize)
    }
}
