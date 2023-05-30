package woowacourse.shopping.repository

import woowacourse.shopping.domain.order.Order
import woowacourse.shopping.domain.order.Payment

interface OrderRepository {
    fun save(cartItemIds: List<Long>, onFinish: (Long) -> Unit)

    fun findById(id: Long, onFinish: (Order) -> Unit)

    fun findAll(onFinish: (List<Order>) -> Unit)

    fun findDiscountPolicy(price: Int, memberGrade: String, onFinish: (Payment) -> Unit)
}
