package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dataSource.OrderDataSource
import woowacourse.shopping.data.dto.OrderHistoryDto
import woowacourse.shopping.data.dto.OrderRequest
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.model.OrderInfo
import woowacourse.shopping.repository.OrderRepository

class OrderRepositoryImpl(
    private val remoteDatabase: OrderDataSource,
) : OrderRepository {

    override fun getOrderInfo(
        ids: List<Int>,
        onSuccess: (OrderInfo) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        remoteDatabase.getOrderItemsInfo(
            ids,
            { onSuccess(it.toDomain()) },
            onFailure,
        )
    }

    override fun postOrder(
        ids: List<Int>,
        usedPoints: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        remoteDatabase.postOrderItem(OrderRequest(ids, usedPoints), onSuccess, onFailure)
    }

    override fun getOrderHistoryList(
        onSuccess: (List<OrderHistory>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        remoteDatabase.getOrderHistories(
            { onSuccess(it.orders.map(OrderHistoryDto::toDomain)) },
            onFailure,
        )
    }

    override fun getOrderHistory(
        id: Int,
        onSuccess: (OrderHistory) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        remoteDatabase.getOrderHistory(id, { onSuccess(it.toDomain()) }, onFailure)
    }
}
