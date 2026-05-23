package woowacourse.shopping.data.remote.server.repository

import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.dto.order.OrderRequest

interface OrderRepository {
    suspend fun order(request: OrderRequest): ApiResult<Unit>
}