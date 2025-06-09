package woowacourse.shopping.data.order.datasource

import woowacourse.shopping.data.order.remote.dto.OrderRequestDto

interface OrderRemoteDataSource {
    suspend fun postOrders(orderRequestDto: OrderRequestDto)
}
