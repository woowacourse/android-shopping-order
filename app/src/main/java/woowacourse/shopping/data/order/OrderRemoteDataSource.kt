package woowacourse.shopping.data.order

import woowacourse.shopping.data.common.HandleResponseResult.handleResponseResult
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.remote.order.OrderApiService
import woowacourse.shopping.remote.order.OrderRequest

class OrderRemoteDataSource(private val orderApiService: OrderApiService) : OrderDataSource {
    override fun order(cartItemIds: List<Long>): ResponseResult<Unit> =
        handleResponseResult { orderApiService.createOrder(OrderRequest(cartItemIds)).execute() }
}
