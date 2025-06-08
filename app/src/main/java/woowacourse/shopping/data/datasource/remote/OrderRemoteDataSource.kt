package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.request.OrderRequestDto
import woowacourse.shopping.data.service.OrderApiService
import java.net.HttpURLConnection

class OrderRemoteDataSource(
    private val orderApiService: OrderApiService,
) {
    suspend fun submitOrder(cartItemIds: List<Int>): Result<Unit> {
        val response = orderApiService.submitOrder(body = OrderRequestDto(cartItemIds))

        return if (response.code() == HttpURLConnection.HTTP_CREATED) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
        }
    }
}
