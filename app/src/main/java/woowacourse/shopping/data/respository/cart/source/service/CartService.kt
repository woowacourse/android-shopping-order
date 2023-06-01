package woowacourse.shopping.data.respository.cart.source.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.CartRemoteEntity

interface CartService {
    @GET("/cart-items")
    fun requestDatas(
        @Header("Authorization")
        token: String
    ): Call<List<CartRemoteEntity>>

    @POST("/cart-items")
    fun requestPostCartItem(
        @Header("Authorization")
        token: String,
        @Body
        productId: Long
    ): Call<Unit>

    @PATCH("/cart-items/{cartItemId}")
    fun requestPatchCartItem(
        @Header("Authorization")
        token: String,
        @Path("cartItemId")
        cartItemId: Long,
        @Body
        quantity: Int
    ): Call<Unit>

    @DELETE("/cart-items/{cartItemId}")
    fun requestDeleteCartItem(
        @Header("Authorization")
        token: String,
        @Path("cartItemId")
        cartItemId: Long,
    ): Call<Unit>
}
