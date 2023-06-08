package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.dto.ProductDto

interface RetrofitProductService {
    @GET("products")
    fun getProducts(): Call<List<ProductDto>>

    @GET("products/{productId}")
    fun getProduct(
        @Path("productId") productId: Int
    ): Call<List<ProductDto>>
}
