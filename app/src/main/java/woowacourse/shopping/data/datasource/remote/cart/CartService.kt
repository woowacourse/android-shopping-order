package woowacourse.shopping.data.datasource.remote.cart

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.CartProductDto

interface CartService {
    @GET("/cart-items")
    fun getAll(): Call<List<CartProductDto>>

    @POST("/cart-items")
    fun addProduct(@Body productId: Int): Call<Unit>

    @PATCH("/cart-items/{cartItemId}")
    fun updateQuantity(
        @Path("cartItemId") cartItemId: Int,
        @Body quantity: Int
    ): Call<Unit>

    @DELETE("/cart-items/{cartItemId}")
    fun deleteProduct(@Path("cartItemId") cartItemId: Int): Call<Unit>
}
