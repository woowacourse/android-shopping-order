package woowacourse.shopping.data.remote.datasource.order

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.OrderRequest

interface OrderDataSource {

    suspend fun postOrders(orderRequest: OrderRequest): Response<Unit>
}