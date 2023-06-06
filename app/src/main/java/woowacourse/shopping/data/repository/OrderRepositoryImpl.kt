package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.order.OrderDataSource
import woowacourse.shopping.data.remote.order.response.OrderDataModel
import woowacourse.shopping.data.remote.order.response.OrderDetailDataModel
import woowacourse.shopping.data.remote.order.response.OrderRequestDataModel

class OrderRepositoryImpl(private val orderRequestDataSource: OrderDataSource) : OrderRepository {
    override fun order(
        orderRequestDataModel: OrderRequestDataModel,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        orderRequestDataSource.order(
            orderRequestDataModel,
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
