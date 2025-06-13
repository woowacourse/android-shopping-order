package woowacourse.shopping.data.source.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.model.ProductsResponse

interface ProductsApiService {
    @GET("/products")
    suspend fun getProducts(
        @Header("accept") accept: String = "*/*",
        @Query("size") size: Int? = 20,
        @Query("page") page: Int?,
    ): Response<ProductsResponse>

    @GET("/products/{id}")
    suspend fun getProductById(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Long,
    ): Response<ProductResponse>

    @GET("/products")
    suspend fun getProductsByCategory(
        @Header("accept") accept: String = "*/*",
        @Query("size") size: Int? = null,
        @Query("page") page: Int? = null,
        @Query("category") category: String,
    ): Response<ProductsResponse>
}
