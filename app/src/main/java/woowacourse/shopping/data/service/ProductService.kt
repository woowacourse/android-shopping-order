package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.common.PageableResponse
import woowacourse.shopping.data.model.product.ProductResponse

interface ProductService {
    @GET("/products")
    fun fetchProducts(
        @Query("category") category: String?,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<PageableResponse<ProductResponse>>

    @GET("/products/{id}")
    fun fetchProduct(
        @Path("id") productId: Long,
    ): Call<ProductResponse>
}
