package woowacourse.shopping.presentation.view.orderdetail

import okio.IOException
import retrofit2.HttpException
import woowacourse.shopping.R
import woowacourse.shopping.presentation.mapper.toUIModel
import woowacouse.shopping.data.repository.order.OrderRepository
import woowacouse.shopping.model.cart.CartProducts

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
) : OrderDetailContract.Presenter {
    override fun loadOrderDetail(orderId: Long) {
        orderRepository.loadOrder(orderId, ::onFailure) { orderDetail ->
            val orderProducts = CartProducts(orderDetail.products)

            view.showOrderProductItemView(orderProducts.getAll().map { it.toUIModel() })
            view.showOrderDateView(orderDetail.orderDateTime)
            view.showOrderPriceView(orderProducts.totalPrice)
            view.showUsedPointView(orderDetail.usedPoint)
            view.showSavedPointView(orderDetail.savedPoint)
            view.showTotalPriceView(orderProducts.totalPrice - orderDetail.usedPoint)
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
