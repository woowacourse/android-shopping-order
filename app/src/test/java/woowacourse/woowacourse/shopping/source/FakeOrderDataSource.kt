package woowacourse.woowacourse.shopping.source

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import woowacourse.shopping.data.model.OrderItemData
import woowacourse.shopping.data.source.OrderDataSource

class FakeOrderDataSource(
    private val orderItems: MutableList<OrderItemData> = mutableListOf(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
) : OrderDataSource {
    override suspend fun order(): Result<Unit> = runCatching {
        orderItems.clear()
    }

    override suspend fun saveOrderItem(orderItemData: OrderItemData): Result<Unit> = runCatching {
        val found = orderItems.find { it.cartItemId == orderItemData.cartItemId }

        if (found == null) {
            orderItems.add(orderItemData)
        } else {
            orderItems.remove(found)
            orderItems.add(orderItemData)
        }
    }

    override suspend fun saveOrderItems(orderItemsData: List<OrderItemData>): Result<Unit> = runCatching {
        orderItemsData.forEach {
            saveOrderItem(it)
        }
    }

    override suspend fun removeOrderItem(cartItemId: Long): Result<Unit> = runCatching {
        orderItems.removeAll { it.cartItemId == cartItemId }
    }

    override suspend fun loadAllOrderItems(): Result<List<OrderItemData>> = runCatching {
        orderItems
    }

    override suspend fun clear(): Result<Unit> = runCatching {
        orderItems.clear()
    }

}