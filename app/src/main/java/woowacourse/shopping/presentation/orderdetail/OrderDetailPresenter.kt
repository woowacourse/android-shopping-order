package woowacourse.shopping.presentation.orderdetail

import android.util.Log
import woowacourse.shopping.data.mapper.toPresentation
import woowacourse.shopping.data.order.OrderRepository

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository
) :
    OrderDetailContract.Presenter {
    override fun loadOrderDetail(orderId: Int) {
        orderRepository.loadOrderDetail(
            orderId,
            onSuccess =
            {
                Log.d("test", it.orderItemResponses.size.toString())
                view.showOrderDetail(it.toPresentation())
            },
            onFailure = {
                Log.d("test2", "실패")
            }
        )
    }
}
