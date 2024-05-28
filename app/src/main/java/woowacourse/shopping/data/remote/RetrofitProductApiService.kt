package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.remote.dto.ProductDto
import woowacourse.shopping.data.remote.dto.ProductResponse

class RetrofitProductApiService : ProductApiService {
    private val retrofitService: RetrofitService =
        Retrofit.Builder()
            .baseUrl("http://54.180.95.212:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)

    override fun loadById(productId: Long): Call<ProductDto> {
        return retrofitService.requestProductDetail(productId = productId.toInt())
    }

    override fun load(
        startPage: Int,
        pageSize: Int,
    ): Call<ProductResponse> {
        return retrofitService.requestProducts(page = startPage, size = pageSize)
    }
}
