package woowacourse.shopping.data.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.product.ProductContent
import woowacourse.shopping.data.dto.product.ProductsResponse

interface ProductService {
    @GET("/products/{id}")
    suspend fun requestProductById(
        @Path("id") id: Long,
    ): Response<ProductContent>

    @GET("/products")
    suspend fun requestProducts(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("category") category: String? = null,
    ): Response<ProductsResponse>
}
