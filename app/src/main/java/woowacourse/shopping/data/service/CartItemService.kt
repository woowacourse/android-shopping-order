package woowacourse.shopping.data.service

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.cartitem.ProductResponse
import woowacourse.shopping.data.dto.cartitem.Quantity
import woowacourse.shopping.data.dto.cartitem.UpdateCartItemRequest

interface CartItemService {
    @GET("/cart-items")
    suspend fun requestCartItems(
        @Query("page") page: Int? = 0,
        @Query("size") size: Int? = 1,
        @Query("sort") sort: List<String> = listOf(),
    ): ProductResponse

    @POST("/cart-items")
    suspend fun postCartItem(
        @Body request: UpdateCartItemRequest,
    )

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") cartItemId: Long,
    )

    @PATCH("/cart-items/{id}")
    suspend fun patchCartItemQuantity(
        @Path("id") cartItemId: Long,
        @Body quantity: Quantity,
    )

    @GET("/cart-items/counts")
    suspend fun getCartItemsCount(): Quantity
}
