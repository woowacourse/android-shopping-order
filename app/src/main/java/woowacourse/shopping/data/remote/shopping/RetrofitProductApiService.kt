package woowacourse.shopping.data.remote.shopping

import retrofit2.Call
import woowacourse.shopping.data.remote.ApiClient
import woowacourse.shopping.data.remote.dto.response.ProductDto
import woowacourse.shopping.data.remote.dto.response.ProductResponse

class RetrofitProductApiService : ProductApiService {
    private val retrofitProductService: RetrofitProductService =
        ApiClient.getApiClient().create(RetrofitProductService::class.java)

    override fun loadById(productId: Long): Call<ProductDto> {
        return retrofitProductService.requestProductDetail(productId = productId.toInt())
    }

    override fun load(
        startPage: Int,
        pageSize: Int,
    ): Call<ProductResponse> {
        return retrofitProductService.requestProducts(page = startPage, size = pageSize)
    }
}
