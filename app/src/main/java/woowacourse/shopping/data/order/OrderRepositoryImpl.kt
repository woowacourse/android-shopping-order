package woowacourse.shopping.data.order

import woowacourse.shopping.data.order.requestbody.OrderCartRequestBody
import woowacourse.shopping.data.order.requestbody.OrderRequestBody
import woowacourse.shopping.data.order.response.OrderDataModel
import woowacourse.shopping.data.order.response.OrderDetailDataModel
import woowacourse.shopping.data.order.response.OrderRequestDataModel

class OrderRepositoryImpl(private val orderRequestDataSource: OrderDataSource) : OrderRepository {
    override fun order(
        orderRequestDataModel: OrderRequestDataModel,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        orderRequestDataSource.order(
            OrderRequestBody(
                spendPoint = orderRequestDataModel.spendPoint,
                orderItems = orderRequestDataModel.orderItems.map {
                    OrderCartRequestBody(it.productId, it.quantity)
                }
            ),
            onSuccess = {
                onSuccess()
            }, onFailure = {
            onFailure()
        }
        )
    }

    override fun loadOrderList(onSuccess: (List<OrderDataModel>) -> Unit, onFailure: () -> Unit) {
        orderRequestDataSource.loadOrderList(onSuccess = {
            onSuccess(it)
        }, onFailure = {
            onFailure()
        })
    }

    override fun loadOrderDetail(
        id: Int,
        onSuccess: (OrderDetailDataModel) -> Unit,
        onFailure: () -> Unit
    ) {
        orderRequestDataSource.loadOrderDetail(id, onSuccess = {
            onSuccess(it)
        }, onFailure = {
            onFailure()
        })
    }
}
