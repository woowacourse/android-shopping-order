package woowacourse.shopping.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.response.ProductDetailResponse
import woowacourse.shopping.data.model.response.ProductsResponse

interface ProductApi {
    @GET("/products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<ProductsResponse>

    @GET("/products/{id}")
    suspend fun getProductDetail(
        @Path("id") id: Long,
    ): Response<ProductDetailResponse>
}
