package woowacourse.shopping.data.cart.source

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CartItemRemoteService {

    @Headers("Content-Type: application/json")
    @POST("cart-items")
    fun requestToSave(
        @Body requestBody: CartItemSaveRequestBody
    ): Call<Unit>

    @GET("cart-items")
    fun requestCartItems(): Call<List<NetworkCartItem>>

    @Headers("Content-Type: application/json")
    @PATCH("cart-items/{cartItemId}")
    fun requestToUpdateQuantity(
        @Path("cartItemId") cartItemId: Long,
        @Body requestBody: CartItemQuantityUpdateRequestBody
    ): Call<Unit>

    @DELETE("cart-items/{cartItemId}")
    fun requestToDelete(
        @Path("cartItemId") cartItemId: Long,
    ): Call<Unit>
}

data class CartItemSaveRequestBody(val productId: Long)

data class CartItemQuantityUpdateRequestBody(val quantity: Int)
