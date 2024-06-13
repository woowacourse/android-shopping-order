package woowacourse.woowacourse.shopping.source

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import woowacourse.shopping.data.model.OrderItemData
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.woowacourse.shopping.testfixture.runCatchingWithDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class FakeOrderDataSource(
    private val orderItems: MutableList<OrderItemData> = mutableListOf(),
    private val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()
) : OrderDataSource {
    override suspend fun order(): Result<Unit> = runCatchingWithDispatcher(dispatcher) {
        orderItems.clear()

    }

    override suspend fun saveOrderItem(orderItemData: OrderItemData): Result<Unit> =
        runCatchingWithDispatcher(dispatcher) {
            val found = orderItems.find { it.cartItemId == orderItemData.cartItemId }

            if (found == null) {
                orderItems.add(orderItemData)
            } else {
                orderItems.remove(found)
                orderItems.add(orderItemData)
            }
        }

    override suspend fun saveOrderItems(orderItemsData: List<OrderItemData>): Result<Unit> =
        runCatchingWithDispatcher(dispatcher) {
            orderItemsData.forEach {
                saveOrderItem(it)
            }
        }

    override suspend fun removeOrderItem(cartItemId: Long): Result<Unit> = runCatchingWithDispatcher(dispatcher) {
        orderItems.removeAll { it.cartItemId == cartItemId }
    }

    override suspend fun loadAllOrderItems(): Result<List<OrderItemData>> = runCatchingWithDispatcher(dispatcher) {
        orderItems
    }

    override suspend fun clear(): Result<Unit> = runCatchingWithDispatcher(dispatcher) {
        orderItems.clear()
    }

}