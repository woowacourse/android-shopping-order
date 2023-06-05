package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.dto.OrderHistoryDto
import woowacourse.shopping.data.dto.OrderInfoDto
import woowacourse.shopping.data.dto.OrderListResponse
import woowacourse.shopping.data.dto.OrderRequest

interface OrderDataSource {
    fun getOrderItemsInfo(ids: List<Int>, callback: (OrderInfoDto?) -> Unit)
    fun postOrderItem(orderRequest: OrderRequest, callback: () -> Unit)
    fun getOrderHistories(callback: (OrderListResponse?) -> Unit)
    fun getOrderHistory(id: Int, callback: (OrderHistoryDto?) -> Unit)
}
