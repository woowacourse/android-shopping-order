package woowacourse.shopping.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.response.ProductDetailResponse
import woowacourse.shopping.data.model.response.ProductsResponse

interface ProductApi {
    @GET("/products")
    fun getProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null,
    ): Call<ProductsResponse>

    @GET("/products/{id}")
    fun getProductDetail(
        @Path("id") id: Long,
    ): Call<ProductDetailResponse>
}
