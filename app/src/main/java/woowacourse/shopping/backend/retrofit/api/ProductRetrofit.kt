package woowacourse.shopping.backend.retrofit.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.backend.retrofit.dto.Product
import woowacourse.shopping.backend.retrofit.dto.ProductResponse

interface ProductRetrofit {
    @GET("/products")
    suspend fun requestProducts(
        @Header("Accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null,
        @Query("category") category: String? = null,
    ): Response<ProductResponse>

    @GET("/products/{id}")
    suspend fun requestProductDetail(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Long,
    ): Response<Product>

    @POST("/products")
    suspend fun addProduct(
        @Header("Accept") accept: String = "*/*",
        @Body product: Product,
    ): Response<Unit>

    @DELETE("/products/{id}")
    suspend fun deleteProduct(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Long,
    ): Response<Unit>
}
