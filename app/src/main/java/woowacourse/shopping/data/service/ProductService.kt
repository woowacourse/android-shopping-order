package woowacourse.shopping.data.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.product.ProductContent
import woowacourse.shopping.data.dto.product.ProductResponse

interface ProductService {
    @GET("/products")
    suspend fun requestProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1,
    ): Response<ProductResponse>

    @GET("/products/{id}")
    suspend fun requestDetailProduct(
        @Path("id") id: Long = 0,
    ): Response<ProductContent>
}
