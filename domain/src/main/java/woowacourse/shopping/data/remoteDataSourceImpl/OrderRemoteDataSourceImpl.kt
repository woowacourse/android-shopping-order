package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.remoteDataSource.OrderRemoteDataSource
import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.model.PostOrderRequest
import woowacourse.shopping.utils.RetrofitUtil

class OrderRemoteDataSourceImpl : OrderRemoteDataSource {
    private var credentials = "Basic YUBhLmNvbToxMjM0"

    private var lastOrderId: Long = 0

    override fun getOrder(cartIds: List<Int>, callback: (Result<Order>) -> Unit) {
        RetrofitUtil.retrofitOrderService.getOrderList(credentials, cartIds.joinToString(","))
            .enqueue(RetrofitUtil.callback(callback))
    }

    override fun getOrderHistoriesNext(callback: (Result<List<OrderHistory>>) -> Unit) {
        when (lastOrderId) {
            0L -> getOrderHistoriesFirst(callback)
            else -> getOrderHistories(callback)
        }
    }

    private fun getOrderHistoriesFirst(callback: (Result<List<OrderHistory>>) -> Unit) {
        RetrofitUtil.retrofitOrderService
            .getOrders(credentials)
            .enqueue(
                RetrofitUtil.callback { result ->
                    result.onSuccess {
                        callback(Result.success(it.orderHistories))
                        lastOrderId = it.lastOrderId
                    }.onFailure { e -> callback(Result.failure(e)) }
                }
            )
    }

    private fun getOrderHistories(callback: (Result<List<OrderHistory>>) -> Unit) {
        RetrofitUtil.retrofitOrderService
            .getOrdersNext(credentials, lastOrderId)
            .enqueue(
                RetrofitUtil.callback { result ->
                    result.onSuccess {
                        callback(Result.success(it.orderHistories))
                        lastOrderId = it.lastOrderId
                    }.onFailure { e -> callback(Result.failure(e)) }
                }
            )
    }

    override fun getOrderHistory(id: Long, callback: (Result<OrderHistory>) -> Unit) {
        RetrofitUtil.retrofitOrderService.getOrder(credentials, id)
            .enqueue(RetrofitUtil.callback(callback))
    }

    override fun postOrder(point: Int, cartIds: List<Int>, callback: (Result<Long>) -> Unit) {
        RetrofitUtil.retrofitOrderService
            .postOrder(credentials, PostOrderRequest(point, cartIds))
            .enqueue(RetrofitUtil.callbackWithLocationHeader(callback))
    }
}
