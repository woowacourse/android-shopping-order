package woowacourse.shopping.data.remote.retrofit.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.retrofit.dto.Product
import woowacourse.shopping.data.remote.retrofit.dto.ProductResponse

interface ProductRetrofitInterface {
    @GET("/products")
    suspend fun requestProducts(
        @Header("Accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null,
        @Query("category") category: String? = null,
    ): ProductResponse

    @GET("/products/{id}")
    suspend fun requestProductDetail(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Long,
    ): Product

    @POST("/products")
    suspend fun addProduct(
        @Header("Accept") accept: String = "*/*",
        @Body product: Product,
    ): Unit

    @DELETE("/products/{id}")
    suspend fun deleteProduct(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Long,
    ): Unit
}
