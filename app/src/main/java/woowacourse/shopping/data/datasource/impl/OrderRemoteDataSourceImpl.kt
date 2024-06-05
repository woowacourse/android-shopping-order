package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.OrderRemoteDataSource
import woowacourse.shopping.data.dto.request.RequestOrdersPostDto
import woowacourse.shopping.data.service.OrderService

class OrderRemoteDataSourceImpl(private val service: OrderService) : OrderRemoteDataSource {
    override suspend fun order(request: RequestOrdersPostDto): Result<Unit> =
        runCatching {
            service.postOrders(request = request)
        }
}
