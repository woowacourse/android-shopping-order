package woowacourse.shopping.feature.order.list

import com.example.domain.model.BaseResponse
import com.example.domain.model.OrderMinInfoItem
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toPresentation

class OrderListPresenter(
    private val view: OrderListContract.View,
    private val orderRepository: OrderRepository,
) : OrderListContract.Presenter {
    override fun loadOrderItems() {
        orderRepository.fetchAllOrders { result ->
            when (result) {
                is BaseResponse.SUCCESS -> {
                    val orders = result.response
                    view.setOrderListItems(orders.map(OrderMinInfoItem::toPresentation))
                }
                is BaseResponse.FAILED -> view.showFailedLoadOrderList()
                is BaseResponse.NETWORK_ERROR -> view.showNetworkError()
            }
        }
    }

    override fun requestOrderDetail(orderId: Long) {
        view.showOrderDetail(orderId)
    }
}
