package woowacourse.shopping.repository

import woowacourse.shopping.domain.Order

interface OrderRepository {

    fun findById(id: Long, onFinish: (Order) -> Unit)
}
