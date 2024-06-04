package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.NetworkResult

interface OrderRepository {
    fun completeOrder(
        cartItemIds: List<Long>,
        callBack: (result: NetworkResult<Unit>) -> Unit,
    )
}
