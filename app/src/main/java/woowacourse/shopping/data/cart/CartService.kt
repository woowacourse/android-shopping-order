package woowacourse.shopping.data.cart

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CartService {
    @GET("cart-items")
    fun getAllCartItems(
        @Header("Authorization") credentials: String,
    ): Call<List<CartDataModel>>

    @Headers("Content-Type: application/json")
    @POST("cart-items")
    fun addCartItem(
        @Header("Authorization") credentials: String,
        @Body addCartRequestBody: AddCartRequestBody,
    ): Call<Unit>

    @DELETE("cart-items/{cartItemId}")
    fun deleteCartItem(
        @Header("Authorization") credentials: String,
        @Path("cartItemId") cartItemId: Int,
    ): Call<CartDataModel>

    @Headers("Content-Type: application/json")
    @PATCH("cart-items/{cartItemId}")
    fun updateCartItemCount(
        @Header("Authorization") credentials: String,
        @Path("cartItemId") cartItemId: Int,
        @Body quantityBody: UpdateQuantityRequestBody,
    ): Call<Unit>
}
