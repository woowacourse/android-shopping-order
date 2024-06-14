package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.OrderItemData

interface OrderDataSource {
    suspend fun order(): Result<Unit>

    suspend fun saveOrderItem(orderItemData: OrderItemData): Result<Unit>

    suspend fun saveOrderItems(orderItemsData: List<OrderItemData>): Result<Unit>

    suspend fun removeOrderItem(cartItemId: Long): Result<Unit>

    suspend fun loadAllOrderItems(): Result<List<OrderItemData>>

    suspend fun clear(): Result<Unit>
}
