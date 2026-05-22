package woowacourse.shopping.data.remote.retrofit.repository

import woowacourse.shopping.data.remote.retrofit.api.OrderRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo

class OrderRetrofitRepository(
    private val apiService: OrderRetrofitInterface,
) {
    suspend fun order(orderInfo: OrderInfo) =
        apiService.order(
            order =
                OrderInfo(
                    cartItemIds = orderInfo.cartItemIds,
                ),
        )
}
