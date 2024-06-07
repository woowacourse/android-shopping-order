package woowacourse.shopping.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse

interface ProductApiService {
    @GET("/products")
    suspend fun requestProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 0,
    ): Response<ProductResponse>

    @GET("/products")
    suspend fun requestCategoryProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("category") category: String,
        @Query("size") size: Int = 0,
    ): Response<ProductResponse>

    @GET("/products/{id}")
    suspend fun requestProduct(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Response<ProductDto>
}
