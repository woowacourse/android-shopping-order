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

interface CartItemRemoteService1 {

    @Headers("Content-Type: application/json")
    @POST("cart-items")
    fun requestToSave(
        @Header("Authorization") authorization: String,
        @Body requestBody: CartItemSaveRequestBody
    ): Call<Unit>

    @GET("cart-items")
    fun requestCartItems(@Header("Authorization") authorization: String): Call<List<CartItemDto>>

    @Headers("Content-Type: application/json")
    @PATCH("cart-items/{cartItemId}")
    fun requestToUpdateCount(
        @Path("cartItemId") cartItemId: Long,
        @Header("Authorization") authorization: String,
        @Body requestBody: CartItemQuantityUpdateRequestBody
    ): Call<Unit>

    @DELETE("cart-items/{cartItemId}")
    fun requestToDelete(
        @Path("cartItemId") cartItemId: Long,
        @Header("Authorization") authorization: String
    ): Call<Unit>
}

data class CartItemSaveRequestBody(val productId: Long)

data class CartItemQuantityUpdateRequestBody(val quantity: Int)
