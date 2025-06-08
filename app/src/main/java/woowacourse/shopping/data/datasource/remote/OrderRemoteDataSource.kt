package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.request.OrderRequestDto
import woowacourse.shopping.data.service.OrderApiService

class OrderRemoteDataSource(
    private val orderApiService: OrderApiService,
) {
    suspend fun submitOrder(cartItemIds: List<Int>): Result<Unit> {
        return orderApiService.submitOrder(body = OrderRequestDto(cartItemIds))
    }
}
