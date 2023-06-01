package woowacourse.shopping.repository

import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment
import woowacourse.shopping.domain.user.User

interface OrderRepository {
    fun save(cartItemIds: List<Long>, user: User): Result<Long>

    fun findById(id: Long, user: User): Result<Order>

    fun findAll(user: User): Result<List<Order>>

    fun findDiscountPolicy(price: Int, memberGrade: String): Result<Payment>
}
