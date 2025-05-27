package woowacourse.shopping.data.product.api

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.product.model.ProductResponse

interface ProductsApiService {
    @GET("/products")
    fun getProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1
    ): Call<ProductResponse>

    @POST("/products")
    fun postProducts(
        @Header("accept") accept: String = "*/*",
    ): String

    @GET("/products/{id}")
    fun getProductsId(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Call<ProductResponse>

    @DELETE("/products/{id}")
    fun deleteProductsId(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    ):String
}