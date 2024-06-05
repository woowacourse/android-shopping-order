package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.dto.OrderRequest

interface OrderDataSource {
    fun postOrder(orderRequest: OrderRequest): Result<Unit>
}
