package woowacourse.shopping.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import woowacourse.shopping.data.model.response.ProductsResponse

interface ProductApi {
    @GET("/products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ProductsResponse
}
