package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.remoteDataSource.OrderRemoteDataSource
import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderList
import woowacourse.shopping.utils.RetrofitUtil

class OrderRemoteDataSourceImpl : OrderRemoteDataSource {
    private var credentials = "Basic cmljaEBtYWlsLmNvbToxMjM0"

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
}
