package woowacourse.shopping.data.order

import woowacourse.shopping.data.NetworkResult
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(private val remoteOrderDataSource: RemoteOrderDataSource) : OrderRepository {
    override fun completeOrder(
        cartItemIds: List<Long>,
        callBack: (result: NetworkResult<Unit>) -> Unit,
    ) {
        val cartIds = cartItemIds.map { it.toInt() }
        remoteOrderDataSource.requestOrder(cartIds, callBack)
    }
}
