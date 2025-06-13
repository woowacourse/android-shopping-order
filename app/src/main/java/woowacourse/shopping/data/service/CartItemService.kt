package woowacourse.shopping.data.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemResponse
import woowacourse.shopping.data.model.response.Quantity

interface CartItemService {
    @GET("/cart-items")
    suspend fun getCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): Response<CartItemResponse>

    @POST("/cart-items")
    suspend fun postCartItem(
        @Body body: CartItemRequest,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") cartId: Long,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun patchCartItem(
        @Path("id") cartId: Long,
        @Body body: Quantity,
    ): Response<Unit>

    @GET("/cart-items/counts")
    suspend fun getCartItemsCount(): Response<Quantity>
}
