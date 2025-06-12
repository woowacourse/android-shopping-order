package woowacourse.shopping.data.product.service

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.product.dto.ProductResponse
import woowacourse.shopping.data.product.dto.ProductsResponse

interface ProductService {
    @GET("/products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ProductsResponse?

    @GET("/products/{id}")
    suspend fun getProductById(
        @Path("id") id: Long,
    ): ProductResponse?

    @GET("/products")
    suspend fun getProductsByCategory(
        @Query("category") category: String,
    ): ProductsResponse?
}
