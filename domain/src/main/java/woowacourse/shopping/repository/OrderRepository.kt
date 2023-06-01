package woowacourse.shopping.repository

import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.domain.user.User

interface OrderRepository {
    fun save(cartItemIds: List<Long>, user: User, onFinish: (Result<Long>) -> Unit)

    fun findById(id: Long, user: User, onFinish: (Result<Order>) -> Unit)

    fun findAll(user: User, onFinish: (Result<List<Order>>) -> Unit)

    fun findDiscountPolicy(price: Int, memberGrade: String, onFinish: (Result<Payment>) -> Unit)
}
