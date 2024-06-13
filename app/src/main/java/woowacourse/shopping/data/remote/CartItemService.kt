package woowacourse.shopping.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.cart.remote.AddCartItemRequest
import woowacourse.shopping.data.cart.remote.CartItemQuantityRequest
import woowacourse.shopping.data.cart.remote.CartResponse
import woowacourse.shopping.data.cart.remote.CountResponse

interface CartItemService {
    @GET("/cart-items")
    suspend fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): Result<CartResponse>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") id: Int = 0,
    ): Result<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun setCartItemQuantity(
        @Path("id") id: Int = 0,
        @Body quantity: CartItemQuantityRequest,
    ): Result<Unit>

    @GET("/cart-items/counts")
    suspend fun requestCartQuantityCount(): Result<CountResponse>

    @POST("/cart-items")
    suspend fun addCartItem(
        @Body addCartItemRequest: AddCartItemRequest,
    ): Result<Unit>
}
