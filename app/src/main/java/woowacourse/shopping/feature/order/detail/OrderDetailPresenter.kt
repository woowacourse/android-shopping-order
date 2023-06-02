package woowacourse.shopping.feature.order.detail

import com.example.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.util.toMoneyFormat
import java.time.format.DateTimeFormatter

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
    private val orderId: Long,
) : OrderDetailContract.Presenter {
    override fun loadOrderInfo() {
        orderRepository.getOrderDetailById(
            orderId,
            onSuccess = { orderDetail ->
                val orderDetailUiModel = orderDetail.toPresentation()
                view.setOrderProductsInfo(orderDetailUiModel.orderItems)
                view.setOrderDate(orderDetailUiModel.dateTime.format(dateTimeFormatter))
                val saleAmount: Int =
                    orderDetailUiModel.priceBeforeDiscount - orderDetailUiModel.priceAfterDiscount
                view.setOrderPaymentInfo(
                    orderDetailUiModel.priceBeforeDiscount.toMoneyFormat(),
                    saleAmount.toMoneyFormat(),
                    orderDetailUiModel.priceAfterDiscount.toMoneyFormat()
                )
            },
            onFailure = {}
        )
    }

    companion object {
        private const val dateTimePattern = "yyyy.M.d"
        private val dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern)
    }
}
