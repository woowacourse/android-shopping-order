package woowacourse.shopping.data.order

import woowacourse.shopping.data.HandleResponseResult.handleResponseResult
import woowacourse.shopping.data.ResponseResult
import woowacourse.shopping.remote.order.OrderApiService
import woowacourse.shopping.remote.order.OrderRequest

class OrderRemoteDataSource(private val orderApiService: OrderApiService) : OrderDataSource {
    override fun order(cartItemIds: List<Long>): ResponseResult<Unit> =
        handleResponseResult { orderApiService.createOrder(OrderRequest(cartItemIds)).execute() }
}
