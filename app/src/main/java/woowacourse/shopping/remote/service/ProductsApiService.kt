package woowacourse.shopping.remote.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.model.ProductDto
import woowacourse.shopping.remote.model.ProductResponse

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
