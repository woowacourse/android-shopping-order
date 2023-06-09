package woowacourse.shopping.presentation.view.orderlist

import okio.IOException
import retrofit2.HttpException
import woowacourse.shopping.R
import woowacourse.shopping.presentation.mapper.toUIModel
import woowacouse.shopping.data.repository.order.OrderRepository

class OrderListPresenter(
    private val view: OrderListContract.View,
    private val orderRepository: OrderRepository,
) : OrderListContract.Presenter {
    override fun loadOrderList() {
        orderRepository.loadOrderList(::onFailure) { orders ->
            view.showOrderListItemView(orders.map { it.toUIModel() })
        }
    }

    private fun onFailure(throwable: Throwable) {
        val messageId = when (throwable) {
            is IOException -> { R.string.toast_message_network_error }
            is HttpException -> { R.string.toast_message_http_error }
            else -> { R.string.toast_message_system_error }
        }
        view.handleErrorView(messageId)
    }
}
