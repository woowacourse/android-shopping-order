package woowacourse.shopping.data.remote.retrofit.api

import retrofit2.Call
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
    fun requestProducts(
        @Header("Accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null,
        @Query("category") category: String? = null,
    ): Call<ProductResponse>

    @GET("/products/{id}")
    fun requestProductDetail(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Long,
    ): Call<Product>

    @POST("/products")
    fun addProduct(
        @Header("Accept") accept: String = "*/*",
        @Body product: Product,
    ): Call<Void>

    @DELETE("/products/{id}")
    fun deleteProduct(
        @Header("Accept") accept: String = "*/*",
        @Path("id") id: Long,
    ): Call<Void>
}
