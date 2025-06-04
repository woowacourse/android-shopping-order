package woowacourse.shopping.data.network.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.network.response.products.ProductResponse
import woowacourse.shopping.data.network.response.products.ProductsResponse

interface ProductService {
    @GET("/products")
    fun singlePage(
        @Query("category") category: String?,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
    ): Call<ProductsResponse>

    @GET("/products/{id}")
    suspend fun getProduct(
        @Path("id") id: Long,
    ): ProductResponse
}
