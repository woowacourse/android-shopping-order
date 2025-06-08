package woowacourse.shopping.data.service

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.response.ProductDto
import woowacourse.shopping.data.dto.response.ProductResponseDto

interface ProductApiService {
    @GET("/products")
    suspend fun getPagedProducts(
        @Query("page") page: Int?,
        @Query("size") size: Int?,
    ): Result<ProductResponseDto>

    @GET("/products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int,
    ): Result<ProductDto>
}
