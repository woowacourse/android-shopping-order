package woowacourse.shopping.data.service

import okhttp3.ResponseBody
import retrofit2.Response
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
    suspend fun fetchCartItems(
        @Header("Authorization") key: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<PageableResponse<CartItemResponse>>

    @POST("/cart-items")
    suspend fun addCartItem(
        @Header("Authorization") key: String,
        @Body addCartItemCommand: AddCartItemCommand,
    ): Response<ResponseBody>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Header("Authorization") key: String,
        @Path("id") cartId: Long,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun patchCartItemQuantity(
        @Header("Authorization") key: String,
        @Path("id") cartId: Long,
        @Body quantity: Quantity,
    ): Response<Unit>

    @GET("/cart-items/counts")
    suspend fun fetchCartItem(
        @Header("Authorization") key: String,
    ): Response<Quantity>
}
