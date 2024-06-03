package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.OrderRemoteDataSource
import woowacourse.shopping.data.dto.request.RequestOrdersPostDto
import woowacourse.shopping.data.service.OrderService
import kotlin.concurrent.thread

class OrderRemoteDataSourceImpl(private val service: OrderService) : OrderRemoteDataSource {
    override fun order(request: RequestOrdersPostDto): Result<Unit> =
        runCatching {
            thread {
                service.postOrders(request = request).execute().body()
            }.join()
        }
}
