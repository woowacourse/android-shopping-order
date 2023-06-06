package woowacourse.shopping.data.remote.order

import woowacourse.shopping.data.remote.order.response.OrderDataModel
import woowacourse.shopping.data.remote.order.response.OrderDetailDataModel
import woowacourse.shopping.data.remote.order.response.OrderRequestDataModel

interface OrderDataSource {
    fun order(orderRequest: OrderRequestDataModel, onSuccess: () -> Unit, onFailure: () -> Unit)
    fun loadOrderList(onSuccess: (List<OrderDataModel>) -> Unit, onFailure: () -> Unit)
    fun loadOrderDetail(id: Int, onSuccess: (OrderDetailDataModel) -> Unit, onFailure: () -> Unit)
}
