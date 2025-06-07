package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.exception.NetworkResult

interface OrderRepository {
    suspend fun createOrder(cartItemIds: List<Long>): NetworkResult<Unit>
}
