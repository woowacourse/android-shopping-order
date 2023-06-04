package woowacourse.shopping.data.dataSource

import woowacourse.shopping.model.OrderInfo

interface OrderDataSource {
    fun getOrderItemsInfo(ids: List<Int>, callback: (OrderInfo?) -> Unit)
    fun postOrderItem(ids: List<Int>, usedPoints: Int, callback: () -> Unit)
}
