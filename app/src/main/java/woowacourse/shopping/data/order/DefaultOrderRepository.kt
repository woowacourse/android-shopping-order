package woowacourse.shopping.data.order

import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.repository.OrderRepository

class DefaultOrderRepository(private val orderDataSource: OrderDataSource) : OrderRepository {
    override fun save(cartItemIds: List<Long>, onFinish: (Long) -> Unit) {
        orderDataSource.save(cartItemIds, onFinish)
    }

    override fun findById(id: Long, onFinish: (Order) -> Unit) {
        orderDataSource.findById(id, onFinish)
    }

    override fun findAll(onFinish: (List<Order>) -> Unit) {
        orderDataSource.findAll(onFinish)
    }

    override fun findDiscountPolicy(price: Int, memberGrade: String, onFinish: (Payment) -> Unit) {
        orderDataSource.findDiscountPolicy(price, memberGrade, onFinish)
    }
}
