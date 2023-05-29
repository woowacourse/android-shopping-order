package woowacourse.shopping.data.repository

import woowacourse.shopping.ui.model.OrderRecord

// todo: 위치가 어디에?
interface OrderRepository {

    fun getOrderRecord(
        orderId: Int,
        onReceived: (orderRecord: OrderRecord) -> Unit,
    )
}
