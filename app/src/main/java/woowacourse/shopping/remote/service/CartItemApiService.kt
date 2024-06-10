package woowacourse.shopping.remote.service

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.model.request.CartItemRequest
import woowacourse.shopping.remote.model.response.CartItemListResponse

interface CartItemApiService {
    @GET("/cart-items")
    suspend fun requestCartItems2(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): CartItemListResponse

    @POST("/cart-items")
    suspend fun addCartItem2(
        @Body cartItemRequest: CartItemRequest,
    )

    @PATCH("/cart-items/{id}")
    suspend fun updateCartItemQuantity2(
        @Path("id") id: Long,
        @Body quantity: Int,
    )

    @DELETE("/cart-items/{id}")
    suspend fun removeCartItem2(
        @Path("id") id: Long,
    )
}
