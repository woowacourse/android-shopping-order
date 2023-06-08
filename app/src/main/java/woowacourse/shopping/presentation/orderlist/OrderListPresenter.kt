package woowacourse.shopping.presentation.orderlist

import woowacourse.shopping.data.mapper.toPresentation
import woowacourse.shopping.data.repository.OrderRepository

class OrderListPresenter(
    private val view: OrderListContract.View,
    private val orderRepository: OrderRepository
) : OrderListContract.Presenter {
    override fun loadOrderList() {
        orderRepository.loadOrderList(onSuccess = { orders ->
            view.showOrderList(
                orders.map {
                    it.toPresentation()
                }
            )
        }, onFailure = {
        })
    }
}
