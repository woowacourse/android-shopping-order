package woowacourse.shopping.data.remote.product

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {
    @GET("/products")
    suspend fun requestGoods(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): ProductResponse

    @GET("/products/{productId}")
    suspend fun requestProductDetails(
        @Path("productId") productId: Long,
    ): ProductResponse.Content
}
