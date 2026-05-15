package woowacourse.shopping.data.remote.server.service

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.server.dto.product.ProductResponse
import woowacourse.shopping.data.remote.server.dto.products.ProductsResponse

interface ProductService {
    @GET("products")
    suspend fun requestProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ProductsResponse

    @GET("products/{id}")
    suspend fun requestProduct(
        @Path("id") id: Long,
    ): ProductResponse

    @GET("products")
    suspend fun requestCategoryProducts(
        @Query("category") category: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
    ): ProductsResponse

//    @POST("orders")
//    suspend fun
}
