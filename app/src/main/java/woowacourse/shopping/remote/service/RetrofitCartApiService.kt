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

interface RetrofitCartApiService : CartApiService {
    @GET("/cart-items")
    override suspend fun requestCartItems(
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): CartItemListResponse

    @POST("/cart-items")
    override suspend fun addCartItem(
        @Body cartItemRequest: CartItemRequest,
    )

    @PATCH("/cart-items/{id}")
    override suspend fun updateCartItemQuantity(
        @Path("id") id: Long,
        @Body quantity: Int,
    )

    @DELETE("/cart-items/{id}")
    override suspend fun removeCartItem(
        @Path("id") id: Long,
    )

}
