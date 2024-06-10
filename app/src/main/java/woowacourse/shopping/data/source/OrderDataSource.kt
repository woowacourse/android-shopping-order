package woowacourse.shopping.data.source

import woowacourse.shopping.domain.model.OrderItem

interface OrderDataSource2 {
    suspend fun order(): Result<Unit>

    suspend fun saveOrderItem(
        orderItem: OrderItem
    ): Result<Unit>

    suspend fun removeOrderItem(
        orderItem: OrderItem
    ): Result<Unit>

    suspend fun removeOrderItem(
        cartItemId: Long
    ): Result<Unit>

    suspend fun loadAllOrders(): Result<List<OrderItem>>

    suspend fun clear(): Result<Unit>
}

