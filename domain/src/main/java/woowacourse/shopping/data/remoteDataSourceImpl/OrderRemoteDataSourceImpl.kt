package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.remoteDataSource.OrderRemoteDataSource
import woowacourse.shopping.model.OrderList
import woowacourse.shopping.utils.RetrofitUtil

class OrderRemoteDataSourceImpl : OrderRemoteDataSource {
    override fun getAll(callback: (Result<OrderList>) -> Unit) {
        RetrofitUtil.retrofitOrderService.getOrderList().enqueue(RetrofitUtil.callback(callback))
    }
}
