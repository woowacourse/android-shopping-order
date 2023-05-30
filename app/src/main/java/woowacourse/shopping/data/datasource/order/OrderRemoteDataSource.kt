package woowacourse.shopping.data.datasource.order

import woowacourse.shopping.data.model.DataOrderRecord
import woowacourse.shopping.data.model.OrderRequest

interface OrderRemoteDataSource {

    fun addOrder(
        orderRequest: OrderRequest,
        onAdded: (orderId: Long) -> Unit,
    )

    fun getOrderRecord(
        orderId: Int,
        onReceived: (DataOrderRecord) -> Unit,
    )
}
