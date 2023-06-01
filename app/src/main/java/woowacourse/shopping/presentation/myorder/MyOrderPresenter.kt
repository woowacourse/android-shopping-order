package woowacourse.shopping.presentation.myorder

import android.util.Log
import woowacourse.shopping.presentation.mapper.toPresentation
import woowacourse.shopping.presentation.model.OrderModel
import woowacourse.shopping.repository.OrderRepository

class MyOrderPresenter constructor(
    private val view: MyOrderContract.View,
    private val orderRepository: OrderRepository
) : MyOrderContract.Presenter {

    override fun loadOrders() {
        orderRepository.getAllOrders { orders ->
            Log.d("wooseok", orders.toString())
            val orderModels = orders.map { it.toPresentation() }
            view.setOrders(orderModels)
        }
    }

    override fun showOrderDetail(orderModel: OrderModel) {
        view.navigateToOrderDetail(orderModel.orderId)
    }
}
