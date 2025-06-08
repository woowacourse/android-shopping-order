package woowacourse.shopping.data.datasource.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.dto.request.OrderRequestDto
import woowacourse.shopping.data.service.OrderApiService

class OrderRemoteDataSource(
    private val orderService: OrderApiService,
) {
    suspend fun insert(cartIds: List<Int>): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                orderService.insert(OrderRequestDto(cartIds))
                Unit
            }
        }
}
