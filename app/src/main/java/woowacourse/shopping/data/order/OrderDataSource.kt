package woowacourse.shopping.data.order

import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.domain.user.User

interface OrderDataSource {
    fun save(cartItemIds: List<Long>, user: User, onFinish: (Long) -> Unit)

    fun findById(id: Long, user: User, onFinish: (Order) -> Unit)

    fun findAll(user: User, onFinish: (List<Order>) -> Unit)

    fun findDiscountPolicy(price: Int, memberGrade: String, onFinish: (Payment) -> Unit)
}
