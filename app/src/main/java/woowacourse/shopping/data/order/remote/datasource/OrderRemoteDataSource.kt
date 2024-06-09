package woowacourse.shopping.data.order.remote.datasource

import woowacourse.shopping.data.common.ResponseHandlingUtils.handleExecute
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.order.remote.OrderApiService
import woowacourse.shopping.data.order.remote.dto.OrderRequest

class OrderRemoteDataSource(private val orderApiService: OrderApiService) : OrderDataSource {
    override suspend fun orderCartItems(cartItemIds: List<Long>): ResponseResult<Unit> =
        handleExecute { orderApiService.createOrder(OrderRequest(cartItemIds)) }
}
