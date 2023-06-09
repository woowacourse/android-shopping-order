package woowacourse.shopping.data.datasource.remote.orderdetail

import woowacourse.shopping.data.remote.request.OrderDTO

interface OrderRemoteDetailSource {
    fun getById(orderId: Long, callback: (Result<OrderDTO>) -> Unit)
}
