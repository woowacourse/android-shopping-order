package woowacourse.shopping.data.product

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductRemoteService1 {

    @GET("products")
    fun requestProducts(): Call<List<ProductDto>>

    @GET("products/{productId}")
    fun requestProduct(@Path("productId") productId: Long): Call<ProductDto>
}
