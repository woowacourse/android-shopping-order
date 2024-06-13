package woowacourse.shopping.remote.service

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.dto.ProductDto
import woowacourse.shopping.remote.dto.ProductResponse

interface ProductService {
    @GET("/products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
    ): ProductResponse

    @GET("/products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int,
    ): ProductDto
}
