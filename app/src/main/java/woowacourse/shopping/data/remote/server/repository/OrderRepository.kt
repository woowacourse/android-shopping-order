package woowacourse.shopping.data.remote.server.repository

import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.domain.Order

interface OrderRepository {
    suspend fun order(order: Order): ApiResult<Unit>
}
