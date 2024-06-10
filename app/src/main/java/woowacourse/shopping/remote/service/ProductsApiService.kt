package woowacourse.shopping.remote.service

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.model.response.ProductListResponse
import woowacourse.shopping.remote.model.response.ProductResponse

interface ProductsApiService {
    @GET("/products")
    suspend fun requestProducts2(
        @Query("category") category: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): ProductListResponse

    @GET("/products/{id}")
    suspend fun requestProduct2(
        @Path("id") id: Int,
    ): ProductResponse
}
