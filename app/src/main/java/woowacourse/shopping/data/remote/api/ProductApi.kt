package woowacourse.shopping.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.response.product.ProductResponse
import woowacourse.shopping.data.remote.dto.response.products.ProductsResponse

interface ProductApi {
    @GET("/products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String> = emptyList(),
    ): ProductsResponse

    @GET("/products")
    suspend fun getProductsByCategory(
        @Query("category") category: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String> = emptyList(),
    ): ProductsResponse

    @GET("/products/{id}")
    suspend fun getProductById(
        @Path("id") id: Long,
    ): ProductResponse
}
