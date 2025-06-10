package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.order.OrderRequest
import woowacourse.shopping.data.service.OrderService

class OrderRemoteDataSourceImpl(
    private val orderService: OrderService,
) : OrderRemoteDataSource {
    override suspend fun order(cartIds: List<Long>): Result<Unit> =
        handleApiCall(
            errorMessage = "주문 실패",
            apiCall = { orderService.requestOrder(OrderRequest(cartIds)) },
            transform = { response ->
                response.body() ?: throw IllegalStateException("응답 바디가 null입니다.")
            },
        )
}
