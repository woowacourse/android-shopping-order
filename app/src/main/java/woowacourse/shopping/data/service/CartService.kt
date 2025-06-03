package woowacourse.shopping.data.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.common.PageableResponse

interface CartService {
    @GET("/cart-items")
    fun fetchCartItems(
        @Header("Authorization") key: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<PageableResponse<CartItemResponse>>

    @POST("/cart-items")
    fun addCartItem(
        @Header("Authorization") key: String,
        @Body addCartItemCommand: AddCartItemCommand,
    ): Call<ResponseBody>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Header("Authorization") key: String,
        @Path("id") cartId: Long,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun patchCartItemQuantity(
        @Header("Authorization") key: String,
        @Path("id") cartId: Long,
        @Body quantity: Quantity,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun fetchCartItem(
        @Header("Authorization") key: String,
    ): Call<Quantity>
}
