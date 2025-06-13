package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.request.OrderRequest

interface OrderDataSource {
    suspend fun postOrder(request: OrderRequest): Result<Unit>
}
