package woowacourse.shopping.data.service.cart

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.dto.response.CartProductDto

interface CartRetrofitApi {
    @GET("cart-items")
    fun requestCarts(@Header("Authorization") authorization: String): Call<List<CartProductDto>>

    @POST("cart-items")
    fun requestAddCartProduct(
        @Header("Authorization") authorization: String,
        @Body body: RequestBody,
    ): Call<Unit>

    @PATCH("cart-items/{cartId}")
    fun requestChangeCartProductCount(
        @Header("Authorization") authorization: String,
        @Path("cartId") cartId: Long,
        @Body body: RequestBody,
    ): Call<Unit>

    @DELETE("cart-items/{cartId}")
    fun requestDeleteCartProduct(
        @Header("Authorization") authorization: String,
        @Path("cartId") cartId: Long,
    ): Call<Unit>
}
