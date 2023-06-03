package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.model.Product

interface RetrofitProductService {
    @GET("products")
    fun getProducts(): Call<List<Product>>

    @GET("products/{productId}")
    fun getProduct(
        @Path("productId") productId: Int
    ): Call<List<Product>>
}
