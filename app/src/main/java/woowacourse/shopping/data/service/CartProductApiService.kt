package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.request.CartProductQuantityRequestDto
import woowacourse.shopping.data.dto.request.CartProductRequestDto
import woowacourse.shopping.data.dto.response.CartProductQuantityResponseDto
import woowacourse.shopping.data.dto.response.CartProductResponseDto

interface CartProductApiService {
    @GET("/cart-items")
    fun getPagedProducts(
        @Header("accept") accept: String = "application/json",
        @Query("page") page: Int?,
        @Query("size") size: Int?,
    ): Call<CartProductResponseDto>

    @POST("/cart-items")
    fun insert(
        @Body body: CartProductRequestDto,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun delete(
        @Path("id") id: Int,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun getTotalQuantity(
        @Header("accept") accept: String = "application/json",
    ): Call<CartProductQuantityResponseDto>

    @PATCH("/cart-items/{id}")
    fun updateQuantity(
        @Path("id") id: Int,
        @Body body: CartProductQuantityRequestDto,
    ): Call<Unit>
}
