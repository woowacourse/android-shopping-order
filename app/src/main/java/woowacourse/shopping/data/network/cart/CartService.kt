package woowacourse.shopping.data.network.cart

import retrofit2.Call
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
    fun requestCartItems(
        @Query("page")
        page: Int = 0,
        @Query("size")
        size: Int = 1,
    ): Call<CartItemsResponse>

    @POST("/cart-items")
    fun insertCartItem(
        @Body
        request: CartItemAddRequest,
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
        quantity: QuantityDto,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun getCartItemTotalCount(): Call<QuantityDto>
}
