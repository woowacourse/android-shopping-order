package woowacourse.shopping.data.remote.datasource.order

import woowacourse.shopping.data.remote.dto.Message
import woowacourse.shopping.data.remote.dto.request.OrderRequest

interface OrderDataSource {
    suspend fun post(orderRequest: OrderRequest): Result<Message<Unit>>
}
