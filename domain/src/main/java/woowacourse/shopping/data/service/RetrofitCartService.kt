package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.dto.CartProductDto
import woowacourse.shopping.model.ProductIdBody
import woowacourse.shopping.model.QuantityBody

interface RetrofitCartService {
    @GET("cart-items")
    fun getCarts(): Call<List<CartProductDto>>

    @POST("cart-items")
    fun postCart(
        @Body productId: ProductIdBody
    ): Call<Void>

    @PATCH("cart-items/{cartId}")
    fun patchCart(
        @Path("cartId") cartId: Int,
        @Body quantity: QuantityBody
    ): Call<Void>

    @DELETE("cart-items/{cartId}")
    fun deleteCart(
        @Path("cartId") cartId: Int
    ): Call<Void>
}
