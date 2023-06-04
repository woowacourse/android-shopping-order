package woowacourse.shopping.data.server

import woowacourse.shopping.domain.OrderHistory

interface MemberRemoteDataSource {
    fun getPoints(onSuccess: (Int) -> Unit, onFailure: () -> Unit)

    fun getHistories(onSuccess: (List<OrderHistory>) -> Unit, onFailure: () -> Unit)
}