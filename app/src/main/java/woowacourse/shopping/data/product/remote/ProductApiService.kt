package woowacourse.shopping.data.product.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.response.ProductDto
import woowacourse.shopping.data.dto.response.ProductResponse

interface ProductApiService {
    @GET("/products")
    suspend fun requestProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ProductResponse

    @GET("/products")
    suspend fun requestProductsWithCategory(
        @Header("accept") accept: String = "*/*",
        @Query("category") category: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ProductResponse

    @GET("/products/{id}")
    suspend fun requestProductDetail(
        @Path(value = "id") productId: Int,
    ): ProductDto
}
