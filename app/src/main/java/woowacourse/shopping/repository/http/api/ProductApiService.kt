package woowacourse.shopping.repository.http.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.repository.http.dto.product.ProductPageResponseDto
import woowacourse.shopping.repository.http.dto.product.ProductResponseDto

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
