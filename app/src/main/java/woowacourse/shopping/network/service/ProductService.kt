package woowacourse.shopping.network.service

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.network.dto.ProductResponse
import woowacourse.shopping.network.dto.ProductsResponse

interface ProductService {
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): ProductsResponse

    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") id: Long,
    ): ProductResponse
}
