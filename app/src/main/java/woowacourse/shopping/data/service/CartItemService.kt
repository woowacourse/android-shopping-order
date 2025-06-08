package woowacourse.shopping.data.service

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.cartitem.CartItemResponse
import woowacourse.shopping.data.model.response.Quantity

interface CartItemService {
    @GET("/cart-items")
    suspend fun getCartItems(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): CartItemResponse

    @POST("/cart-items")
    suspend fun postCartItem(
        @Body body: CartItemRequest,
    )

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") id: Long,
    )

    @PATCH("/cart-items/{id}")
    suspend fun patchCartItem(
        @Path("id") id: Long,
        @Body body: Quantity,
    )

    @GET("/cart-items/counts")
    suspend fun getCartItemsCount(): Quantity
}
