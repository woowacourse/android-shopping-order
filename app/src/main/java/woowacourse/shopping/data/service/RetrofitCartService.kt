package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.dto.CartProductDto

interface RetrofitCartService {
    @GET("cart-items")
    fun getCarts(): Call<List<CartProductDto>>

    @POST("cart-items")
    fun postCart(
        @Body productId: Int,
    ): Call<Unit>

    @PATCH("cart-items/{cartId}")
    fun patchCart(
        @Path("cartId") cartId: Int,
        @Body quantity: Int,
    ): Call<Unit>

    @DELETE("cart-items/{cartId}")
    fun deleteCart(
        @Path("cartId") cartId: Int,
    ): Call<String>
}
