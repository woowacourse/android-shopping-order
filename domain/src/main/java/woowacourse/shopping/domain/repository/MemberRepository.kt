package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.OrderHistory

interface MemberRepository {
    fun getPoints(onSuccess: (Int) -> Unit, onFailure: () -> Unit)

    fun getHistories(onSuccess: (List<OrderHistory>) -> Unit, onFailure: () -> Unit)
}