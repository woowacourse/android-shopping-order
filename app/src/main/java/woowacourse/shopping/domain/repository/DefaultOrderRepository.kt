package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.data.source.ProductDataSource

class DefaultOrderRepository(
    private val orderSource: OrderDataSource,
    private val productSource: ProductDataSource,
) : OrderRepository {
    override fun order(cartItemIds: List<Long>) {
        orderSource.order(cartItemIds)
        orderSource.claer()
    }

    override fun saveOrderItem(cartItemId: Long, quantity: Int) {
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


    companion object {
        private const val TAG = "OrderRepository"
    }
}
