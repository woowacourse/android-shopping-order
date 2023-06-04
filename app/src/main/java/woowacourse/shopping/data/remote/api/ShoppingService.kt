package woowacourse.shopping.data.remote.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import woowacourse.shopping.data.remote.response.ProductResponseDto

interface ShoppingService {
    @GET("products")
    fun getProducts(
        @Query("limit") limit: Int,
        @Query("scroll-count") scrollCount: Int,
    ): Call<List<ProductResponseDto>>
}
