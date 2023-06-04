package woowacourse.shopping.data.server

import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.OrderHistory

interface MemberRemoteDataSource {
    fun getPoints(onSuccess: (Int) -> Unit, onFailure: () -> Unit)

    fun getOrderHistories(onSuccess: (List<OrderHistory>) -> Unit, onFailure: () -> Unit)

    fun getOrder(id: Int, onSuccess: (Order) -> Unit, onFailure: () -> Unit)
}