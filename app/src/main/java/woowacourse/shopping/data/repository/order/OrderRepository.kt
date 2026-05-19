package woowacourse.shopping.data.repository.order

import woowacourse.shopping.domain.OrderResult

interface OrderRepository {
    suspend fun orders(cartItemIds: List<String>): OrderResult
}
