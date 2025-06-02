package woowacourse.shopping.data.shoppingCart.remote.service

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.shoppingCart.remote.dto.CartCountsResponse
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemQuantityRequest
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemRequest
import woowacourse.shopping.data.shoppingCart.remote.dto.ShoppingCartItemsResponse

interface ShoppingCartService {
    @GET("cart-items/counts")
    suspend fun getCartCounts(): CartCountsResponse

    @POST("cart-items")
    suspend fun postCartItem(
        @Body cartItemRequest: CartItemRequest,
    )

    @PATCH("cart-items/{id}")
    suspend fun updateCartItemQuantity(
        @Path("id") shoppingCartId: Long,
        @Body cartItemQuantityRequest: CartItemQuantityRequest,
    )

    @GET("cart-items")
    suspend fun getCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ShoppingCartItemsResponse

    @DELETE("cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") shoppingCartId: Long,
    )
}
