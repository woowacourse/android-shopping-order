package woowacourse.shopping.data.respository.cart.source.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.CartRemoteEntity

interface CartService {
    @GET("/cart-items")
    fun requestDatas(): Call<List<CartRemoteEntity>>

    @POST("/cart-items")
    fun requestPostCartItem(
        @Body
        productId: Long
    ): Call<Unit>

    @PATCH("/cart-items/{cartItemId}")
    fun requestPatchCartItem(
        @Path("cartItemId")
        cartItemId: Long,
        @Body
        quantity: Int
    ): Call<Unit>

    @DELETE("/cart-items/{cartItemId}")
    fun requestDeleteCartItem(
        @Path("cartItemId")
        cartItemId: Long,
    ): Call<Unit>
}
