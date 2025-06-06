package woowacourse.shopping.data.service

import retrofit2.Response
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
    fun requestCartItems(
        @Query("page") page: Int? = 0,
        @Query("size") size: Int? = 1,
        @Query("sort") sort: List<String> = listOf(),
    ): Response<ProductResponse>

    @POST("/cart-items")
    fun postCartItem(
        @Body request: UpdateCartItemRequest,
    ): Response<Void>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Path("id") cartItemId: Long,
    ): Response<Void>

    @PATCH("/cart-items/{id}")
    fun patchCartItemQuantity(
        @Path("id") cartItemId: Long,
        @Body quantity: Quantity,
    ): Response<Void>

    @GET("/cart-items/counts")
    fun getCartItemsCount(): Response<Quantity>
}
