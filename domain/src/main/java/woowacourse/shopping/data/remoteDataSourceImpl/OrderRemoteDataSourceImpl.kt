package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.remoteDataSource.OrderRemoteDataSource
import woowacourse.shopping.model.OrderList
import woowacourse.shopping.utils.RetrofitUtil

class OrderRemoteDataSourceImpl : OrderRemoteDataSource {
    private var credentials = "Basic YUBhLmNvbToxMjM0"

    override fun getOrder(cartIds: List<Int>, callback: (Result<OrderList>) -> Unit) {
        RetrofitUtil.retrofitOrderService.getOrderList(credentials, cartIds.joinToString(","))
            .enqueue(RetrofitUtil.callback(callback))
    }
}
