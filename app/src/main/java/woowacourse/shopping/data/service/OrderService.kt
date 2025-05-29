package woowacourse.shopping.data.service

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.model.request.CartItemIdsRequest

interface OrderService {
    @POST("/orders")
    fun postOrder(
        @Body body: CartItemIdsRequest,
    )
}
