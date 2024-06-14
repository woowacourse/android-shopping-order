package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.remote.dto.request.OrderRequest

interface OrderRepository {
    suspend fun post(orderRequest: OrderRequest): Result<Unit>
}
