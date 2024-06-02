package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.OrderRemoteDataSource
import woowacourse.shopping.remote.api.OrderService
import woowacourse.shopping.remote.model.request.PostOrderRequest

class OrderRemoteDataSourceImpl(
    private val service: OrderService,
) : OrderRemoteDataSource {
    override fun postOrderByIds(cartItemsIds: List<Int>): Result<Unit> =
        runCatching {
            val body = PostOrderRequest(cartItemsIds = cartItemsIds)
            service.postOrder(body).execute()
        }
}
