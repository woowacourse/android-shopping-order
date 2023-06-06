package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.remote.dto.CartProductDTO

interface CartApi {
    @GET("cart-items")
    fun requestCartItems(): Call<List<CartProductDTO>>

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
