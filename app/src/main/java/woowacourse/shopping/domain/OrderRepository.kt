package woowacourse.shopping.domain

import woowacourse.shopping.data.remote.dto.request.OrderRequest

interface OrderRepository {

    suspend fun postOrders(orderRequest: OrderRequest): Result<Unit>
}