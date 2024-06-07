package woowacourse.shopping.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.dto.response.ProductDto
import woowacourse.shopping.remote.dto.response.ProductResponse

interface ProductApiService {
    @GET("/products")
    suspend fun requestProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<ProductResponse>

    @GET("/products")
    suspend fun requestProductsWithCategory(
        @Query("category") category: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<ProductResponse>

    @GET("/products/{id}")
    suspend fun requestProductDetail(
        @Path(value = "id") productId: Int,
    ): Response<ProductDto>
}
