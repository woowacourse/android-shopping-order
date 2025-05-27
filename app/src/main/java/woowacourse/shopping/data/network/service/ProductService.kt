package woowacourse.shopping.data.network.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import woowacourse.shopping.data.network.entity.pages.ProductsResponse

interface ProductService {
    @GET("/products")
    fun requestProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ProductsResponse>
}
