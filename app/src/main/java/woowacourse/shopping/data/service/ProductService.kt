package woowacourse.shopping.data.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.common.PageableResponse
import woowacourse.shopping.data.model.product.ProductResponse

interface ProductService {
    @GET("/products")
    suspend fun fetchProducts(
        @Query("category") category: String?,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<PageableResponse<ProductResponse>>

    @GET("/products/{id}")
    suspend fun fetchProduct(
        @Path("id") productId: Long,
    ): Response<ProductResponse>
}
