package woowacourse.shopping.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductsApiService {
    @GET("/products")
    fun requestProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<ProductResponse>

    @GET("/products/{id}")
    fun requestProduct(
        @Path("id") id: Int,
    ): Call<ProductDto>
}
