package woowacourse.shopping.data.datasource.remote.orderhistory

import woowacourse.shopping.data.remote.request.OrderDTO

interface OrderHistorySource {
    fun getOrderList(): Result<List<OrderDTO>>
}
