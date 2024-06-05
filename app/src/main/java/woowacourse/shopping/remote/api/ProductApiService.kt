package woowacourse.shopping.remote.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.dto.response.ProductDto
import woowacourse.shopping.remote.dto.response.ProductResponse

interface ProductApiService {
    @GET("/products")
    fun requestProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ProductResponse>

    @GET("/products")
    fun requestProductsWithCategory(
        @Query("category") category: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ProductResponse>

    @GET("/products/{id}")
    fun requestProductDetail(
        @Path(value = "id") productId: Int,
    ): Call<ProductDto>
}
