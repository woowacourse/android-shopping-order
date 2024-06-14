package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.OrderRemoteDataSource
import woowacourse.shopping.remote.api.OrderService
import woowacourse.shopping.remote.model.request.PostOrderRequest

class OrderRemoteDataSourceImpl(
    private val service: OrderService,
) : OrderRemoteDataSource {
    override suspend fun postOrderByIds(cartItemIds: List<Int>) {
        val body = PostOrderRequest(cartItemIds = cartItemIds)
        service.postOrder(body)
    }
}
