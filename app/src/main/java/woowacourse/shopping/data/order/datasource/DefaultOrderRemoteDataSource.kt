package woowacourse.shopping.data.order.datasource

import woowacourse.shopping.data.order.remote.dto.OrderRequestDto
import woowacourse.shopping.data.order.remote.service.OrderService

class DefaultOrderRemoteDataSource(
    private val orderService: OrderService,
) : OrderRemoteDataSource {
    override suspend fun postOrders(orderRequestDto: OrderRequestDto) = orderService.postOrders(orderRequestDto)
}
