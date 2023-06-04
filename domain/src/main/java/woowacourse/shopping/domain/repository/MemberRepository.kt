package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.OrderHistory

interface MemberRepository {
    fun getPoints(onSuccess: (Int) -> Unit, onFailure: () -> Unit)

    fun getOrderHistories(onSuccess: (List<OrderHistory>) -> Unit, onFailure: () -> Unit)

    fun getOrder(id: Int, onSuccess: (Order) -> Unit, onFailure: () -> Unit)
}