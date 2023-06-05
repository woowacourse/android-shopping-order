package woowacourse.shopping.data.retrofit

import retrofit2.Call
import retrofit2.http.*
import woowacourse.shopping.domain.model.CartProduct

interface CartApi {
    @GET("cart-items")
    fun requestCartItems(): Call<List<CartProduct>>

    @POST("cart-items")
    fun requestInsertCart(
        @Body body: RequestInsertBody,
    ): Call<Unit>

    @PATCH("cart-items/{id}")
    fun requestUpdateCart(
        @Path("id") id: Int,
        @Body quantity: Int,
    ): Call<Unit>

    @DELETE("cart-items/{id}")
    fun requestDeleteCart(@Path("id") id: Int): Call<Unit>
}

data class RequestInsertBody(val productId: Int, val quantity: Int)
