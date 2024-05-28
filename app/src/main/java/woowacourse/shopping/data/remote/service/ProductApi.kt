package woowacourse.shopping.data.remote.service
import retrofit2.Call
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
    @GET("/products")
    fun getProducts(
        @Header("accept") accept: String = "*/*",
//        @Query("category") category: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1
    ): Call<ProductResponse>

    @POST("/products")
    fun postProduct(
        @Header("accept") accept: String = "*/*",
        @Body productRequest: ProductRequest
    ): Call<Unit>

    @GET("/products/{id}")
    fun getProductById(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int
    ): Call<Product>


    @DELETE("/products/{id}")
    fun deleteProductById(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int
    ): Call<Unit>
}