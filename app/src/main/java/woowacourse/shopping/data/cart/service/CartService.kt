package woowacourse.shopping.data.cart.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.AuthStorage
import woowacourse.shopping.data.cart.dto.CartItemRequest
import woowacourse.shopping.data.cart.dto.CartQuantityResponse
import woowacourse.shopping.data.cart.dto.CartResponse
import woowacourse.shopping.data.product.dto.CartRequest

interface CartService {
    @GET("/cart-items")
    fun getCart(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Header("Authorization") header: String = AuthStorage.authorization,
    ): Call<CartResponse>

    @GET("/cart-items")
    fun getAllCart(
        @Header("Authorization") header: String = AuthStorage.authorization,
    ): Call<CartResponse>

    @POST("/cart-items")
    fun postCartItem(
        @Body request: CartRequest,
        @Header("Authorization") header: String = AuthStorage.authorization,
    ): Call<Unit>

    @DELETE("/cart-items/{cartItemId}")
    fun deleteShoppingCartItem(
        @Path("cartItemId") cartItemId: Long,
        @Header("Authorization") header: String = AuthStorage.authorization,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun patchCartItemQuantity(
        @Path("id") id: Long,
        @Body request: CartItemRequest,
        @Header("Authorization") header: String = AuthStorage.authorization,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun getCartItemQuantity(
        @Header("Authorization") header: String = AuthStorage.authorization,
    ): Call<CartQuantityResponse>
}
