package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.model.order.OrderRequest
import woowacourse.shopping.data.service.OrderService
import woowacourse.shopping.data.util.safeApiCall

class OrderRemoteDataSourceImpl(
    private val orderService: OrderService,
) : OrderRemoteDataSource {
    override suspend fun postOrder(cartProductIds: OrderRequest) =
        safeApiCall {
            orderService.postOrder(cartProductIds)
        }
}
