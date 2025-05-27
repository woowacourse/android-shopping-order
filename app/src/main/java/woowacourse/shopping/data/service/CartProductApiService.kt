package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import woowacourse.shopping.data.dto.response.CartProductResponseDto

interface CartProductApiService {
    @GET("/cart-items")
    fun getPagedProducts(
        @Header("accept") accept: String = "*/*",
        @Header("Authorization") authorization: String = "Basic ",
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<CartProductResponseDto>
}
