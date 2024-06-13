package woowacourse.shopping.data.datasource

import woowacourse.shopping.remote.dto.OrderRequest

interface OrderDataSource {
    suspend fun postOrder(orderRequest: OrderRequest): Result<Unit>
}
