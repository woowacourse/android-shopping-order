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

    override fun saveOrderItemTemp(cartItemId: Long, quantity: Int) {
        orderSource.save(cartItemId, quantity)
    }

    override fun loadOrderItemTemp(): Map<Long, Int> = orderSource.load()

    override fun allOrderItemsTempQuantity(): Int = orderSource.allQuantity()

    override fun tempOrderItemsTotalPrice(): Int {
        val load = orderSource.load()

        val map = load.map { (id, quantity) ->
            productSource.findById(id).price.times(quantity)
        }

        return map.sum()
//        return orderSource.totalPrice()
    }


    companion object {
        private const val TAG = "OrderRepository"
    }
}
