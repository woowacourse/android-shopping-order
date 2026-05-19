package woowacourse.shopping.data.remote.retrofit.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.remote.retrofit.api.OrderRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo

class OrderRetrofitRepository(
    private val apiService: OrderRetrofitInterface,
) {
    suspend fun order(order: OrderInfo): Unit =
        withContext(Dispatchers.IO) {
            apiService.order(
                order = order,
            )
        }
}
