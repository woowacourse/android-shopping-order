package woowacourse.shopping.data.network.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import woowacourse.shopping.data.network.request.CartItemRequest
import woowacourse.shopping.data.network.response.carts.CartsResponse

interface CartService {
    @POST("/cart-items")
    fun addCart(
        @Body request: CartItemRequest,
    ): Call<Unit>

    @GET("/cart-items")
    fun getCartSinglePage(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<CartsResponse>
}
