package woowacourse.shopping.data.datasource.remote.orderhistory

import woowacourse.shopping.data.remote.request.OrderDTO

interface OrderHistoryRemoteSource {
    fun getOrderList(callback: (Result<List<OrderDTO>>) -> Unit)
}
