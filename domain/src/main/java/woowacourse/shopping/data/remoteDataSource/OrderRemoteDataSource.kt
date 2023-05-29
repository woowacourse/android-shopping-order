package woowacourse.shopping.data.remoteDataSource

import woowacourse.shopping.model.OrderList

interface OrderRemoteDataSource {
    fun getAll(callback: (Result<OrderList>) -> Unit)
}
