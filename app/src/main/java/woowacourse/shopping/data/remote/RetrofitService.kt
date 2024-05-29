package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.product.remote.retrofit.Content
import woowacourse.shopping.data.product.remote.retrofit.ProductResponse

interface RetrofitService {
    @GET("/products")
    fun requestProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1,
    ): Call<ProductResponse>

    @GET("/products/{id}")
    fun requestProduct(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int = 0,
    ): Call<Content>
}
