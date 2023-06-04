package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.model.CartProduct

interface RetrofitCartService {
    @GET("cart-items")
    fun getCarts(): Call<List<CartProduct>>

    @GET("cart-items/{cartId}")
    fun getCart(
        @Path("cartId") cartId: Int,
    ): Call<CartProduct>

    @POST("cart-items")
    fun postCart(
        @Body productId: Int,
    ): Call<Int>

    @PATCH("cart-items/{cartId}")
    fun patchCart(
        @Path("cartId") cartId: Int,
        @Body quantity: Int,
    ): Call<Int>

    @DELETE("cart-items/{cartId}")
    fun deleteCart(
        @Path("cartId") cartId: Int,
    ): Call<Int>
}
