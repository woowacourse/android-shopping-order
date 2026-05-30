package woowacourse.shopping.data.datasource.remote.order

import woowacourse.shopping.data.remote.retrofit.api.OrderRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo

class RetrofitOrderRemoteDataSource(
    private val apiService: OrderRetrofitInterface,
) : OrderRemoteDataSource {
    override suspend fun order(orderInfo: OrderInfo) {
        apiService.order(
            orderInfo = orderInfo
        )
    }
}
