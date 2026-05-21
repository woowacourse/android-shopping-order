package woowacourse.shopping.data.remote.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.request.AddCartRequestBody
import woowacourse.shopping.data.remote.dto.request.UpdateCartRequestBody
import woowacourse.shopping.data.remote.dto.response.cart.CartQuantityResponse
import woowacourse.shopping.data.remote.dto.response.cart.CartResponse

interface CartApi {
    @GET("/cart-items")
    suspend fun getCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String> = emptyList(),
    ): CartResponse

    @POST("/cart-items")
    suspend fun addCartItem(
        @Body addCartRequestBody: AddCartRequestBody,
    )

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") id: Long,
    )

    @PATCH("/cart-items/{id}")
    suspend fun updateCartItem(
        @Path("id") id: Long,
        @Body updateCartRequestBody: UpdateCartRequestBody,
    )

    @GET("/cart-items/counts")
    suspend fun getCartItemsQuantity(): CartQuantityResponse
}
