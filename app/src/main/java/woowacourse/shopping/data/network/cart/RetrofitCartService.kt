package woowacourse.shopping.data.network.cart

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.network.cart.dto.CartItemDto
import woowacourse.shopping.data.network.cart.dto.CartItemInsertDto
import woowacourse.shopping.data.network.cart.dto.Quantity

interface RetrofitCartService {
    @GET("/cart-items")
    suspend fun requestCartItems(
        @Query("page")
        page: Int = 0,
        @Query("size")
        size: Int = 1,
    ): Response<CartItemDto>

    @POST("/cart-items")
    suspend fun insertCartItem(
        @Body
        cartItemInsertDto: CartItemInsertDto,
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
        quantity: Quantity,
    ): Response<Unit>

    @GET("/cart-items/counts")
    suspend fun getCartItemTotalCount(): Response<Quantity>
}
