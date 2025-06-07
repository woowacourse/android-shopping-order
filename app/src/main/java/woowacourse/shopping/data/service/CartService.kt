package woowacourse.shopping.data.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
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
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<PageableResponse<CartItemResponse>>

    @POST("/cart-items")
    suspend fun addCartItem(
        @Body addCartItemCommand: AddCartItemCommand,
    ): Response<ResponseBody>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") cartId: Long,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun patchCartItemQuantity(
        @Path("id") cartId: Long,
        @Body quantity: Quantity,
    ): Response<Unit>

    @GET("/cart-items/counts")
    suspend fun fetchCartItem(): Response<Quantity>
}
