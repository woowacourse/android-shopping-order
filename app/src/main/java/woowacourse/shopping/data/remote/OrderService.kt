package woowacourse.shopping.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.model.cart.CartItemIds

interface OrderService {
    @POST("/orders")
    suspend fun postOrder(
        @Body cartItemIds: CartItemIds,
    )
}
