package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.ProductIdBody
import woowacourse.shopping.model.QuantityBody

interface RetrofitCartService {
    @GET("cart-items")
    fun getCarts(
        @Header("Authorization") token: String
    ): Call<List<CartProduct>>

    @POST("cart-items")
    fun postCart(
        @Header("Authorization") token: String,
        @Body productId: ProductIdBody
    ): Call<Int>

    @PATCH("cart-items/{cartId}")
    fun patchCart(
        @Path("cartId") cartId: Int,
        @Header("Authorization") token: String,
        @Body quantity: QuantityBody
    ): Call<Void>

    @DELETE("cart-items/{cartId}")
    fun deleteCart(
        @Path("cartId") cartId: Int,
        @Header("Authorization") token: String
    ): Call<Void>
}
