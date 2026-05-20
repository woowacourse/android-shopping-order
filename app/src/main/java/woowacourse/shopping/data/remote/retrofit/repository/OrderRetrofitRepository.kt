package woowacourse.shopping.data.remote.retrofit.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.remote.retrofit.api.OrderRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo

class OrderRetrofitRepository(
    private val apiService: OrderRetrofitInterface,
) {
    suspend fun order(orderInfo: OrderInfo) =
        withContext(Dispatchers.IO) {
            apiService.order(
                order =
                    OrderInfo(
                        cartItemIds = orderInfo.cartItemIds,
                    ),
            )
        }
}
