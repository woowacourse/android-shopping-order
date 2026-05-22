package woowacourse.shopping.data.datasource.remote.order

import woowacourse.shopping.data.remote.retrofit.api.OrderRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo

class OrderRemoteDataSource(
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
