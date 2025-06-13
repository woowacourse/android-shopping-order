package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.util.api.ApiResult

interface OrderRemoteDataSource {
    suspend fun requestOrder(orderCartIds: List<Int>): ApiResult<Int>
}
