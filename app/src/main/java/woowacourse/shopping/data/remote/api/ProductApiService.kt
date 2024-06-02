package woowacourse.shopping.data.remote.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse

interface ProductApiService {
    @GET("/products")
    fun requestProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 0,
    ): Call<ProductResponse>

    @GET("/products")
    fun requestCategoryProducts(
        @Query("page") page: Int = 0,
        @Query("category") category: String,
        @Query("size") size: Int = 0,
    ): Call<ProductResponse>

    @GET("/products/{id}")
    fun requestProduct(
        @Path("id") id: Int,
    ): Call<ProductDto>
}
