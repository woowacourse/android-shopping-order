package woowacourse.shopping.data.remote.service
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.RetrofitModule
import woowacourse.shopping.data.remote.dto.request.ProductRequest
import woowacourse.shopping.data.remote.dto.response.ProductResponse
import woowacourse.shopping.data.remote.dto.response.ProductsResponse

interface ProductApi {
    @GET("/products")
    suspend fun getProducts(
        @Header("accept") accept: String = "*/*",
        @Query("category") category: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1,
    ): Response<ProductsResponse>

    @POST("/products")
    suspend fun postProduct(
        @Header("accept") accept: String = "*/*",
        @Body productRequest: ProductRequest,
    ): Response<Unit>

    @GET("/products/{id}")
    suspend fun getProductById(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Response<ProductResponse>

    @DELETE("/products/{id}")
    suspend fun deleteProductById(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Response<Unit>

    companion object {
        private var service: ProductApi? = null
        fun service(): ProductApi {
            return service ?: RetrofitModule.defaultBuild.create(ProductApi::class.java)
        }
    }
}
