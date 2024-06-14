package woowacourse.shopping.data.remote.service

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.request.ProductRequest
import woowacourse.shopping.data.remote.dto.response.Product
import woowacourse.shopping.data.remote.dto.response.ProductResponse

interface ProductApi {
    @GET("products")
    suspend fun getProducts(
        @Header("accept") accept: String = "*/*",
        @Query("category") category: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1,
    ): ProductResponse

    @POST("products")
    suspend fun addProduct(
        @Header("accept") accept: String = "*/*",
        @Body productRequest: ProductRequest,
    )

    @GET("products/{id}")
    suspend fun getProductById(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Product

    @DELETE("products/{id}")
    suspend fun deleteProductById(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    )
}
