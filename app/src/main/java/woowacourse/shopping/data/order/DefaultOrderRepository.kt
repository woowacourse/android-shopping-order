package woowacourse.shopping.data.order

import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.OrderRepository

class DefaultOrderRepository(private val orderDataSource: OrderDataSource) : OrderRepository {
    override fun save(cartItemIds: List<Long>, user: User, onFinish: (Long) -> Unit) {
        orderDataSource.save(cartItemIds, user, onFinish)
    }

    override fun findById(id: Long, user: User, onFinish: (Order) -> Unit) {
        orderDataSource.findById(id, user, onFinish)
    }

    override fun findAll(user: User, onFinish: (List<Order>) -> Unit) {
        orderDataSource.findAll(user, onFinish)
    }

    override fun findDiscountPolicy(price: Int, memberGrade: String, onFinish: (Payment) -> Unit) {
        orderDataSource.findDiscountPolicy(price, memberGrade, onFinish)
    }
}
