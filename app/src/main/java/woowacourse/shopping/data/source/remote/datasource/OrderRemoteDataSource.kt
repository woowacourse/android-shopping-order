package woowacourse.shopping.data.source.remote.datasource

import retrofit2.Retrofit
import woowacourse.shopping.data.source.remote.api.OrderRequestBody
import woowacourse.shopping.data.source.remote.api.OrderService

class OrderRemoteDataSource(
    retrofit: Retrofit,
) {
    private val orderService = retrofit.create(OrderService::class.java)

    suspend fun requestOrder(itemIds: List<Long>) = orderService.requestOrder(OrderRequestBody(itemIds))
}
