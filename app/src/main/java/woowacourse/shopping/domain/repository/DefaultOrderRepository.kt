package woowacourse.shopping.domain.repository

import androidx.room.PrimaryKey
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.data.source.ProductDataSource

class DefaultOrderRepository(
    private val orderSource: OrderDataSource,
    private val productDataSource: ProductDataSource,
) : OrderRepository {
    override fun order(cartItemIds: List<Long>) {
        orderSource.order(cartItemIds)
    }

    override fun saveOrderItemTemp(cartItemId: Long, quantity: Int) {
        _orderSaved[cartItemId] = quantity
    }

    override fun loadOrderItemTemp(): Map<Long, Int> = orderSaved

    override fun allOrderItemsTempQuantity(): Int = orderSaved.values.sum()

    override fun tempOrderItemsTotalPrice(): Int = orderSaved.map { (id, quantity) ->
        productDataSource.findById(id).price.times(quantity)
    }.sum()


    companion object {
        private const val TAG = "OrderRepository"

        // TODO: 주석 제거
        // id 와 개수 id 는 상품 아이디? (장바구니 아이디가 아니라)
        private val _orderSaved: MutableMap<Long, Int> = mutableMapOf()
        val orderSaved: Map<Long, Int> get() = _orderSaved
    }
}
