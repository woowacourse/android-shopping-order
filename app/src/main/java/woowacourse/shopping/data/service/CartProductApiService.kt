package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.request.CartProductRequestDto
import woowacourse.shopping.data.dto.response.CartProductQuantityResponseDto
import woowacourse.shopping.data.dto.response.CartProductResponseDto

interface CartProductApiService {
    @GET("/cart-items")
    fun getPagedProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<CartProductResponseDto>

    @POST("/cart-items")
    fun insert(
        @Header("accept") accept: String = "*/*",
        @Body body: CartProductRequestDto,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteByProductId(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun getTotalQuantity(
        @Header("accept") accept: String = "*/*",
    ): Call<CartProductQuantityResponseDto>
}
