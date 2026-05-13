package woowacourse.shopping.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.ProductResponseDto
import woowacourse.shopping.data.remote.dto.ProductsResponseDto

interface ProductApi {
    @GET("products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int,
        @Query("size") size: Int,
        // @Query("sort") sort: List<String> = emptyList(),
    ): ProductsResponseDto

    @GET("products/{id}")
    suspend fun getProductDetail(
        @Path("id") id: Int,
    ): ProductResponseDto
}
