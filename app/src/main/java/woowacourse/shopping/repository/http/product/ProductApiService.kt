package woowacourse.shopping.repository.http.product

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("category") category: String? = null,
    ): Response<ProductPageResponseDto>

    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") id: Long,
    ): Response<ProductResponseDto>
}
