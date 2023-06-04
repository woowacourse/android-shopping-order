package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.model.OrderListResponse
import woowacourse.shopping.data.model.OrderRequest
import woowacourse.shopping.model.OrderInfo

interface OrderDataSource {
    fun getOrderItemsInfo(ids: List<Int>, callback: (OrderInfo?) -> Unit)
    fun postOrderItem(orderRequest: OrderRequest, callback: () -> Unit)
    fun getOrderHistories(callback: (OrderListResponse?) -> Unit)
}
