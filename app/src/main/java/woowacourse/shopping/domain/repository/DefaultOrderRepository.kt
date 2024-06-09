package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource

class DefaultOrderRepository(
    private val orderSource: OrderDataSource,
    private val productSource: ProductDataSource,
    private val cartSource: ShoppingCartDataSource,
) : OrderRepository {
    override fun order(cartItemIds: List<Long>) {
        orderSource.order(cartItemIds)
        orderSource.claer()
    }

    override fun saveOrderItem(
        cartItemId: Long,
        quantity: Int,
    ) {
        orderSource.save(cartItemId, quantity)
    }

    override fun orderItems(): Map<Long, Int> = orderSource.load()

    override fun allOrderItemsQuantity(): Int = orderSource.allQuantity()

    override fun orderItemsTotalPrice(): Int {
        val orders = orderSource.load()

        return orders.map { (id, quantity) ->
            productSource.findById(id).price.times(quantity)
        }.sum()
    }

    override suspend fun order2(cartItemIds: List<Long>): Result<Unit> =
        orderSource.order2(cartItemIds).map {
            orderSource.clear2()
        }

    override suspend fun saveOrderItem2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> = orderSource.save2(cartItemId, quantity)

    override suspend fun orderItems2(): Result<Map<Long, Int>> = orderSource.load2()

    override suspend fun allOrderItemsQuantity2(): Result<Int> = orderSource.allQuantity2()

    override suspend fun orderItemsTotalPrice2(): Result<Int> =
        runCatching {
            val orders = orderSource.load2().getOrThrow()
            val allCartItems = cartSource.loadAllCartItems2().getOrThrow()

            orders.map { (cartItemId, quantity) ->
                val foundCartItem = allCartItems.find { it.id == cartItemId }?: throw NoSuchElementException("No such element found in cart items.")
                foundCartItem.product.price.times(quantity)
            }.sum()
        }

    companion object {
        private const val TAG = "OrderRepository"
    }
}
