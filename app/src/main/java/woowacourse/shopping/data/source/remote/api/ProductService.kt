package woowacourse.shopping.data.source.remote.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.source.remote.dto.product.response.ProductResponse
import woowacourse.shopping.data.source.remote.dto.product.response.ProductsResponse

interface ProductService {
    @GET("/products")
    suspend fun requestProducts(
        @Header("accept") accept: String = "*/*",
        @Query("category") category: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1,
    ): ProductsResponse

    @GET("/products/{id}")
    suspend fun requestProduct(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Long,
    ): ProductResponse
}
