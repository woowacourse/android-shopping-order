package woowacourse.shopping.ui.order.detail

import woowacourse.shopping.data.mapper.toUiModel
import woowacourse.shopping.domain.repository.OrderProductRepository
import woowacourse.shopping.ui.order.detail.OrderDetailContract.Presenter
import woowacourse.shopping.ui.order.detail.OrderDetailContract.View

class OrderDetailPresenter(
    view: View,
    private val orderId: Int,
    private val orderProductRepository: OrderProductRepository,
) : Presenter(view) {

    override fun loadOrderDetailInfo() {
        orderProductRepository.requestSpecificOrder(
            orderId = orderId.toString(),
            onSuccess = { orderResponse ->
                view.showOrderDetailProducts(orderResponse.toUiModel().orderedProducts)
                view.showOrderDetailPaymentInfo(orderResponse.toUiModel().payment)
            },
            onFailure = { },
        )
    }

    override fun navigateToHome(itemId: Int) {
        when (itemId) {
            android.R.id.home -> {
                view.navigateToHome()
            }
        }
    }
}
