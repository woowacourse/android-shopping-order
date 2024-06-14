package woowacourse.shopping.data.order.remote.datasource

import woowacourse.shopping.data.common.ApiResponseHandler.handleApiResponse
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.order.remote.OrderApiService
import woowacourse.shopping.data.order.remote.dto.OrderRequest

class OrderRemoteDataSource(private val orderApiService: OrderApiService) : OrderDataSource {
    override suspend fun orderCartItems(cartItemIds: List<Long>): ResponseResult<Unit> =
        handleApiResponse {
            val orderRequest = OrderRequest(cartItemIds)
            orderApiService.createOrder(orderRequest)
        }
}
