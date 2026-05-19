package woowacourse.shopping.data.network.cart

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.network.cart.dto.CartItemAddRequest
import woowacourse.shopping.data.network.cart.dto.CartItemsResponse
import woowacourse.shopping.data.network.cart.dto.QuantityDto

interface CartService {
    @GET("/cart-items")
    suspend fun requestCartItems(
        @Query("page")
        page: Int = 0,
        @Query("size")
        size: Int = 1,
    ): Response<CartItemsResponse>

    @POST("/cart-items")
    suspend fun insertCartItem(
        @Body
        request: CartItemAddRequest,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id")
        id: String,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun updateCartItemQuantity(
        @Path("id")
        id: String,
        @Body
        quantity: QuantityDto,
    ): Response<Unit>

    @GET("/cart-items/counts")
    suspend fun getCartItemTotalCount(): Response<QuantityDto>
}
