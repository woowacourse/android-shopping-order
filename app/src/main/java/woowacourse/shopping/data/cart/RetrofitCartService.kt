package woowacourse.shopping.data.cart

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.cart.model.dto.response.CartProductResponse

interface RetrofitCartService {
    @GET("cart-items")
    fun requestAllCartProducts(): Call<List<CartProductResponse>>

    @POST("cart-items")
    fun requestAddCartProduct(
        @Body productId: Long
    ): Call<Unit>

    @PATCH("cart-items/{cartItemId}")
    fun requestUpdateQuantity(
        @Path("cartItemId") cartItemId: Long,
        @Body quantity: Int
    ): Call<Unit>

    @DELETE("cart-items/{cartItemId}")
    fun requestDeleteCartProduct(
        @Path("cartItemId") cartItemId: Long
    ): Call<Unit>
}
