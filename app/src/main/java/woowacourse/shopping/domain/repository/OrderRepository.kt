package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.OrderItem
import woowacourse.shopping.domain.model.Orders

interface OrderRepository {
    suspend fun order(cartItemIds: List<Long>): Result<Unit>

    suspend fun saveOrderItem(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun orderItems(): Result<Map<Long, Int>>

    suspend fun allOrderItemsQuantity(): Result<Int>

    suspend fun orderItemsTotalPrice(): Result<Int>
}

interface OrderRepository2 {
    suspend fun order(): Result<Unit>

    suspend fun save(
        orderItem: OrderItem
    ): Result<Unit>

    suspend fun updateOrderItem(
        productId: Long,
        quantity: Int,
    ): Result<Unit>


    suspend fun loadAllOrders(): Result<Orders>

    suspend fun allOrderItemsQuantity(): Result<Int>

    suspend fun orderItemsTotalPrice(): Result<Int>

    suspend fun clear(): Result<Unit>

}


