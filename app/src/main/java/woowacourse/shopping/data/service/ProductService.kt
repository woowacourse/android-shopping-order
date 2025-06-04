package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.product.Content
import woowacourse.shopping.data.dto.product.ProductResponse

interface ProductService {
    @GET("/products")
    fun requestProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1,
    ): Call<ProductResponse>

    @GET("/products/{id}")
    fun requestDetailProduct(
        @Path("id") id: Int = 0,
    ): Call<Content>
}
