package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.OrderRequest

interface OrderDataSource {
    fun postOrder(orderRequest: OrderRequest): Result<Unit>
}
