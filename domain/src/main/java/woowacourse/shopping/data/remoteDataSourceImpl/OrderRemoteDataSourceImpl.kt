package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.remoteDataSource.OrderRemoteDataSource
import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderList
import woowacourse.shopping.model.PostOrderRequest
import woowacourse.shopping.utils.RetrofitUtil

class OrderRemoteDataSourceImpl : OrderRemoteDataSource {
    private var credentials = "Basic YUBhLmNvbToxMjM0"

    override fun getOrderList(cartIds: List<Int>, callback: (Result<OrderList>) -> Unit) {
        RetrofitUtil.retrofitOrderService.getOrderList(credentials, cartIds.joinToString(","))
            .enqueue(RetrofitUtil.callback(callback))
    }

    override fun getOrders(callback: (Result<List<Order>>) -> Unit) {
        RetrofitUtil.retrofitOrderService.getOrders(credentials)
            .enqueue(
                RetrofitUtil.callback { result ->
                    result.onSuccess { callback(Result.success(it.orders)) }
                        .onFailure { e -> callback(Result.failure(e)) }
                }
            )
    }

    override fun getOrder(id: Long, callback: (Result<Order>) -> Unit) {
        RetrofitUtil.retrofitOrderService.getOrder(credentials, id)
            .enqueue(RetrofitUtil.callback(callback))
    }

    override fun postOrder(point: Int, cartIds: List<Int>, callback: (Result<Long>) -> Unit) {
        RetrofitUtil.retrofitOrderService
            .postOrder(credentials, PostOrderRequest(point, cartIds))
            .enqueue(RetrofitUtil.callbackWithLocationHeader(callback))
    }
}
