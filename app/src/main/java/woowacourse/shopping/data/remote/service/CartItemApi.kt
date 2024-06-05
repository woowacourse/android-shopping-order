package woowacourse.shopping.data.remote.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.request.CartItemRequestDto
import woowacourse.shopping.data.remote.dto.request.QuantityRequestDto
import woowacourse.shopping.data.remote.dto.response.CartResponseDto
import woowacourse.shopping.data.remote.dto.response.QuantityResponseDto

interface CartItemApi {
    @GET("/cart-items")
    fun getCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<CartResponseDto>

    @POST("/cart-items")
    fun postCartItem(
        @Header("accept") accept: String = "*/*",
        @Body cartItemRequestDto: CartItemRequestDto,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun patchCartItem(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
        @Body quantityRequestDto: QuantityRequestDto,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun getCartItemsCounts(
        @Header("accept") accept: String = "*/*",
    ): Call<QuantityResponseDto>
}
