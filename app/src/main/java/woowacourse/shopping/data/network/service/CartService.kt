package woowacourse.shopping.data.network.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.network.request.CartItemRequest

interface CartService {
    @POST("/cart-items")
    fun addCarat(
        @Body request: CartItemRequest,
    ): Call<Unit>
}
