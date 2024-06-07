package woowacourse.shopping.data.order

import woowacourse.shopping.data.common.ResponseHandlingUtils.handleExecute
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.remote.order.OrderApiService
import woowacourse.shopping.remote.order.OrderRequest

class OrderRemoteDataSource(private val orderApiService: OrderApiService) : OrderDataSource {
    override fun orderCartItems(cartItemIds: List<Long>): ResponseResult<Unit> =
        handleExecute { orderApiService.createOrder(OrderRequest(cartItemIds)).execute() }
}
