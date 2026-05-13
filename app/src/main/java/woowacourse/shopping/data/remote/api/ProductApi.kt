package woowacourse.shopping.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.request.Pageable
import woowacourse.shopping.data.remote.dto.response.product.ProductResponse
import woowacourse.shopping.data.remote.dto.response.products.ProductsResponse

interface ProductApi {
    @GET("/products")
    suspend fun getProducts(
        @Query("category") category: String,
        @Query("pageable") pageable: Pageable,
    ): ProductsResponse

    @GET("/products/{id}")
    suspend fun getProductById(
        @Path("id") id: Long,
    ): ProductResponse
}
