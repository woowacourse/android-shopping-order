package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.OrderItemData
import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.model.toData
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.OrderItem
import woowacourse.shopping.domain.model.Orders

class DefaultOrderRepository(
    private val orderSource: OrderDataSource,
    private val cartSource: ShoppingCartDataSource,
    private val orderDateTime: OrderDateTime = LoadBasedOrderDateTime(),
) : OrderRepository {
    override suspend fun order(): Result<Unit> = orderSource.order()

    override suspend fun save(orderItem: OrderItem): Result<Unit> = orderSource.saveOrderItem(orderItem.toData())

    override suspend fun save(orderItems: List<OrderItem>): Result<Unit> = orderSource.saveOrderItems(orderItems.toData())

    override suspend fun updateOrderItem(
        productId: Long,
        quantity: Int,
    ): Result<Unit> {
        val cartItem = cartSource.findCartItemByProductId(productId).getOrNull()

        if (cartItem == null) {
            cartSource.addNewProduct(ProductIdsCountData(productId, quantity)).getOrThrow()
            val addedCartItem = cartSource.findCartItemByProductId(productId).getOrThrow()
            return orderSource.saveOrderItem(
                orderItemData =
                    OrderItemData(
                        cartItemId = addedCartItem.id,
                        quantity = 1,
                        product = addedCartItem.product,
                    ),
            )
        }

        if (quantity == 0) {
            cartSource.removeCartItem(cartItem.id).getOrThrow()
            return orderSource.removeOrderItem(cartItem.id)
        }

        cartSource.updateProductsCount(cartItem.id, quantity).getOrThrow()
        return orderSource.saveOrderItem(
            orderItemData =
                OrderItemData(
                    cartItemId = cartItem.id,
                    quantity = quantity,
                    product = cartItem.product,
                ),
        )
    }

    override suspend fun loadAllOrders(): Result<Orders> {
        return orderSource.loadAllOrderItems().map { orderItemsData ->
            Orders(
                orderItems = orderItemsData.toDomain(),
                totalPrice = orderItemsData.sumOf { it.product.price * it.quantity },
                orderDateTime = orderDateTime.dateTime(),
            )
        }
    }

    override suspend fun allOrderItemsQuantity(): Result<Int> =
        orderSource.loadAllOrderItems().map { orderItems ->
            orderItems.sumOf { orderItem -> orderItem.quantity }
        }

    override suspend fun orderItemsTotalPrice(): Result<Int> =
        orderSource.loadAllOrderItems().map { orderItems ->
            orderItems.sumOf { orderItem -> orderItem.product.price * orderItem.quantity }
        }

    companion object {
        private const val TAG = "OrderRepository2"
    }
}
