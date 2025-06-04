package woowacourse.shopping.data.network.service

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.network.request.CartItemRequest
import woowacourse.shopping.data.network.response.carts.CartsResponse

interface CartService {
    @POST("/cart-items")
    suspend fun addCart(
        @Body request: CartItemRequest,
    ): Response<Unit>

    @GET("/cart-items")
    fun getCartSinglePage(
        @Query("page") page: Int?,
        @Query("size") size: Int?,
    ): Call<CartsResponse>

    @PATCH("/cart-items/{id}")
    fun updateCart(
        @Path("id") id: Long,
        @Body request: Int,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteCart(
        @Path("id") id: Long,
    ): Call<Unit>
}
