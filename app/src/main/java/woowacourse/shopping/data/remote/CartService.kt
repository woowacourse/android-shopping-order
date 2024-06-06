package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.CartItemRequestBody
import woowacourse.shopping.data.model.CartQuantity
import woowacourse.shopping.data.model.CartResponse

interface CartService {
    @GET("/cart-items")
    suspend fun getCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
    ): CartResponse

    @POST("/cart-items")
    suspend fun addCartItem(
        @Body cartItemRequestBody: CartItemRequestBody,
    )

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") id: Int,
    )

    @PATCH("/cart-items/{id}")
    suspend fun updateCartItem(
        @Path("id") id: Int,
        @Body cartQuantity: CartQuantity,
    )

    @GET("/cart-items/counts")
    suspend fun getCartTotalQuantity(): CartQuantity
}
