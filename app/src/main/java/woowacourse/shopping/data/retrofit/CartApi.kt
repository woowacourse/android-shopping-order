package woowacourse.shopping.data.retrofit

import retrofit2.Call
import retrofit2.http.*
import woowacourse.shopping.domain.model.CartProduct

interface CartApi {
    @Headers("Authorization: Basic ZG9vbHlAZG9vbHkuY29tOjEyMzQ=")
    @GET("cart-items")
    fun requestCartItems(): Call<List<CartProduct>>

    @Headers("Authorization: Basic ZG9vbHlAZG9vbHkuY29tOjEyMzQ=")
    @POST("cart-items")
    fun requestInsertCart(
        @Body body: RequestInsertBody,
    ): Call<Unit>

    @Headers("Authorization: Basic ZG9vbHlAZG9vbHkuY29tOjEyMzQ=")
    @PATCH("cart-items/{id}")
    fun requestUpdateCart(
        @Path("id") id: Int,
        @Body quantity: Int,
    ): Call<Unit>

    @Headers("Authorization: Basic ZG9vbHlAZG9vbHkuY29tOjEyMzQ=")
    @DELETE("cart-items/{id}")
    fun requestDeleteCart(@Path("id") id: Int): Call<Unit>
}

data class RequestInsertBody(val productId: Int, val quantity: Int)
