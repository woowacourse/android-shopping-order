package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.OrderItem
import woowacourse.shopping.domain.model.Orders
import java.time.LocalDateTime

class DefaultOrderRepository(
    private val orderSource: OrderDataSource,
    private val cartSource: ShoppingCartDataSource,
) : OrderRepository {
    override suspend fun order(): Result<Unit> = orderSource.order()

    override suspend fun save(orderItem: OrderItem): Result<Unit> = orderSource.saveOrderItem(orderItem)

    override suspend fun updateOrderItem(
        productId: Long,
        quantity: Int,
    ): Result<Unit> {
        val cartItem = cartSource.findCartItemByProductId(productId).getOrThrow()

        if (quantity == 0) {
            cartSource.removeCartItem(cartItem.id).getOrThrow()
            return orderSource.removeOrderItem(cartItem.id)
        }

        return orderSource.saveOrderItem(
            orderItem =
                OrderItem(
                    cartItemId = cartItem.id,
                    quantity = quantity,
                    product = cartItem.product.toDomain(),
                ),
        )
    }

    override suspend fun loadAllOrders(): Result<Orders> {
        return orderSource.loadAllOrders().map { orderItems ->
            Orders(
                orderItems = orderItems,
                totalPrice = orderItems.sumOf { it.product.price * it.quantity },
                orderDateTime = LocalDateTime.now(),
            )
        }
    }

    override suspend fun allOrderItemsQuantity(): Result<Int> =
        orderSource.loadAllOrders().map { orderItems ->
            orderItems.sumOf { orderItem -> orderItem.quantity }
        }

    override suspend fun orderItemsTotalPrice(): Result<Int> =
        orderSource.loadAllOrders().map { orderItems ->
            orderItems.sumOf { orderItem -> orderItem.product.price * orderItem.quantity }
        }

    override suspend fun clear(): Result<Unit> = orderSource.clear()

    companion object {
        private const val TAG = "OrderRepository2"
    }
}
