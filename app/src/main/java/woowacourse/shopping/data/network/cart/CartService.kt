package woowacourse.shopping.data.network.cart

import retrofit2.Call
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

interface CartService {
    @GET("/cart-items")
    fun requestCartItems(
        @Query("page")
        page: Int = 0,
        @Query("size")
        size: Int = 1,
    ): Call<CartItemDto>

    @POST("/cart-items")
    fun insertCartItem(
        @Body
        cartItemInsertDto: CartItemInsertDto,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Path("id")
        id: String,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun updateCartItemQuantity(
        @Path("id")
        id: String,
        @Body
        quantity: Quantity,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun getCartItemTotalCount(): Call<Quantity>
}
