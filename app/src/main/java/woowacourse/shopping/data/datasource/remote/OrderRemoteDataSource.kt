package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.request.OrderRequestDto
import woowacourse.shopping.data.service.OrderApiService

class OrderRemoteDataSource(
    private val orderApiService: OrderApiService,
) {
    suspend fun submitOrder(cartItemIds: List<Int>): Result<Unit> {
        val response = orderApiService.submitOrder(body = OrderRequestDto(cartItemIds))

        return if (response.code() == SUCCESS_POST) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
        }
    }

    companion object {
        private const val SUCCESS_POST = 201
    }
}
