package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.payment.OrderRequestResult

interface OrderRemoteDataSource {
    suspend fun requestOrder(orderCartIds: List<Int>): OrderRequestResult<Int>
}
