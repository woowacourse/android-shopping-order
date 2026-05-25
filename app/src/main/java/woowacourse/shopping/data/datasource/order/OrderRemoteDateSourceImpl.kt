package woowacourse.shopping.data.datasource.order

import woowacourse.shopping.data.remote.api.OrderApi
import woowacourse.shopping.data.remote.api.OrderRequest

class OrderRemoteDateSourceImpl(
    private val orderApi: OrderApi,
) : OrderRemoteDataSource {
    override suspend fun order(cartItemIds: List<Int>) = orderApi.order(OrderRequest(cartItemIds))
}
