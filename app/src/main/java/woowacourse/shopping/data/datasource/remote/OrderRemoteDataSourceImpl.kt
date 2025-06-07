package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.order.OrderRequest
import woowacourse.shopping.data.service.OrderService

class OrderRemoteDataSourceImpl(
    private val orderService: OrderService,
) : OrderRemoteDataSource {
    override suspend fun order(cartIds: List<Long>): Result<Unit> =
        handleApiCall(
            errorMessage = "주문 실패",
        ) {
            val request = OrderRequest(cartIds)
            val response = orderService.requestOrder(request)
            if (!response.isSuccessful) {
                throw Exception("API 호출 실패: ${response.code()} ${response.message()}")
            }
        }
}
