package woowacourse.shopping.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.product.remote.Content
import woowacourse.shopping.data.product.remote.ProductResponse

interface ProductService {
    @GET("/products")
    suspend fun requestProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Result<ProductResponse>

    @GET("/products/{id}")
    suspend fun requestProduct(
        @Path("id") id: Int = 0,
    ): Result<Content>
}
