package woowacourse.shopping.data.source

import woowacourse.shopping.domain.model.OrderItem
import woowacourse.shopping.remote.model.request.OrderRequest
import woowacourse.shopping.remote.service.OrderApiService

interface OrderDataSource {
    suspend fun order(cartItemIds: List<Long>): Result<Unit>

    suspend fun save(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun load(): Result<Map<Long, Int>>

    suspend fun allQuantity(): Result<Int>

    suspend fun clear(): Result<Unit>
}


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

