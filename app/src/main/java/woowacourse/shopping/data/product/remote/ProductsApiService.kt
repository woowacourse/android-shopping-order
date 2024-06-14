package woowacourse.shopping.data.product.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.product.remote.dto.ProductDto
import woowacourse.shopping.data.product.remote.dto.ProductResponse

interface ProductsApiService {
    @GET("/products")
    suspend fun requestProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Response<ProductResponse>

    @GET("/products/{id}")
    suspend fun requestProduct(
        @Path("id") id: Int,
    ): Response<ProductDto>
}
