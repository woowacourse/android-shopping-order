package woowacourse.shopping.data.source.remote.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.model.ProductsResponse

interface ProductsApiService {
    @GET("/products")
    fun getProducts(
        @Header("accept") accept: String = "*/*",
        @Query("size") size: Int? = 20,
        @Query("page") page: Int?,
    ): Call<ProductsResponse>

    @GET("/products/{id}")
    fun getProductById(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Long,
    ): Call<ProductResponse>

    @GET("/products")
    fun getProductsByCategory(
        @Header("accept") accept: String = "*/*",
        @Query("size") size: Int? = null,
        @Query("page") page: Int? = null,
        @Query("category") category: String,
    ): Call<ProductsResponse>
}
