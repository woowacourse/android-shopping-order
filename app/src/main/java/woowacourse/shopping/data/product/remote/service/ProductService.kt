package woowacourse.shopping.data.product.remote.service

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.product.remote.dto.ProductResponse
import woowacourse.shopping.data.product.remote.dto.ProductsResponse

interface ProductService {
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ProductsResponse

    @GET("products/{id}")
    suspend fun getProductDetail(
        @Path("id") productId: Long,
    ): ProductResponse
}
