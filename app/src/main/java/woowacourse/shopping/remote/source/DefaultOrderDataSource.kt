package woowacourse.shopping.remote.source

import woowacourse.shopping.data.model.OrderItemData
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.remote.model.request.OrderRequest
import woowacourse.shopping.remote.service.OrderApiService

class DefaultOrderDataSource(
    private val orderApiService: OrderApiService,
) : OrderDataSource {
    override suspend fun order(): Result<Unit> =
        runCatching {
            orderApiService.createOrder2(
                OrderRequest(orderItems.map(OrderItemData::cartItemId)),
            )
        }

    override suspend fun saveOrderItem(orderItemData: OrderItemData): Result<Unit> =
        runCatching {
            val found = orderItems.find { it.cartItemId == orderItemData.cartItemId }

            if (found == null) {
                _orderItems.add(orderItemData)
            } else {
                _orderItems.remove(found)
                _orderItems.add(orderItemData)
            }
        }

    override suspend fun saveOrderItems(orderItemsData: List<OrderItemData>): Result<Unit> =
        runCatching {
            orderItemsData.forEach {
                saveOrderItem(it)
            }
        }

    override suspend fun removeOrderItem(cartItemId: Long): Result<Unit> =
        runCatching {
            _orderItems.removeAll { it.cartItemId == cartItemId }
        }

    override suspend fun loadAllOrderItems(): Result<List<OrderItemData>> =
        runCatching {
            orderItems
        }

    override suspend fun clear(): Result<Unit> =
        runCatching {
            _orderItems.clear()
        }

    companion object {
        private const val TAG = "OrderRemoteDataSource"

        private val _orderItems: MutableList<OrderItemData> = mutableListOf()
        val orderItems: List<OrderItemData> get() = _orderItems
    }
}
