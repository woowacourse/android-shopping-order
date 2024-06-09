package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource

class DefaultOrderRepository(
    private val orderSource: OrderDataSource,
    private val cartSource: ShoppingCartDataSource,
) : OrderRepository {
    override suspend fun order(cartItemIds: List<Long>): Result<Unit> =
        orderSource.order(cartItemIds).map {
            orderSource.clear()
        }

    override suspend fun saveOrderItem(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> = orderSource.save(cartItemId, quantity)

    override suspend fun orderItems(): Result<Map<Long, Int>> = orderSource.load()

    override suspend fun allOrderItemsQuantity(): Result<Int> = orderSource.allQuantity()

    override suspend fun orderItemsTotalPrice(): Result<Int> =
        runCatching {
            val orders = orderSource.load().getOrThrow()
            val allCartItems = cartSource.loadAllCartItems2().getOrThrow()

            orders.map { (cartItemId, quantity) ->
                val foundCartItem = allCartItems.find { it.id == cartItemId }
                    ?: throw NoSuchElementException("No such element found in cart items.")
                foundCartItem.product.price.times(quantity)
            }.sum()
        }

    companion object {
        private const val TAG = "OrderRepository"
    }
}
