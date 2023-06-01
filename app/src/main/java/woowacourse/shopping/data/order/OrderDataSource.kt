package woowacourse.shopping.data.order

import woowacourse.shopping.data.order.requestbody.OrderRequestBody
import woowacourse.shopping.data.order.response.OrderDataModel

interface OrderDataSource {
    fun order(orderRequestBody: OrderRequestBody, onSuccess: () -> Unit, onFailure: () -> Unit)
    fun loadOrderList(onSuccess: (List<OrderDataModel>) -> Unit, onFailure: () -> Unit)
}
