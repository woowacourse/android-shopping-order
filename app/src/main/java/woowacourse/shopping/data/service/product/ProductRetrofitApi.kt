package woowacourse.shopping.data.service.product

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.data.dto.ProductDto

interface ProductRetrofitApi {
    @GET("products")
    fun request(): Call<List<ProductDto>>

    @GET("products/{productId}")
    fun requestProduct(@Path("productId") productId: Long): Call<ProductDto>
}
