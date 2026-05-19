package woowacourse.shopping.data.datasource.order

import woowacourse.shopping.domain.OrderResult

interface OrderDataSource {
    suspend fun orders(cartItemIds: List<String>): OrderResult
}
