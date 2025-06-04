package woowacourse.shopping.data.service

import retrofit2.Response
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
    ): Response<ProductResponseDto>

    @GET("/products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int,
    ): Response<ProductDto>
}
