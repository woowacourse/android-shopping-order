package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.OrderItem
import woowacourse.shopping.domain.model.Orders

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


