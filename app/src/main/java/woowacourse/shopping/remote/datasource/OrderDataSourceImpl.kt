package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.OrderDataSource
import woowacourse.shopping.remote.api.OrderService
import woowacourse.shopping.remote.model.request.PostOrderRequest

class OrderDataSourceImpl(
    private val service: OrderService,
) : OrderDataSource {
    override fun postOrder(cartItemsIds: List<Int>): Result<Unit> =
        runCatching {
            val body = PostOrderRequest(cartItemIds = cartItemsIds)
            service.postOrder(body).execute()
        }
}
