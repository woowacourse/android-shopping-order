package woowacourse.shopping.view.orderdetail

import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.model.toUiModel

class OrderDetailPresenter(
    private val orderId: Int,
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
) : OrderDetailContract.Presenter {
    override fun fetchOrder() {
        orderRepository.getOrder(orderId) { result ->
            when (result) {
                is DataResult.Success -> {
                    val model = result.response.toUiModel()
                    view.showOrderDetail(model)
                }
                is DataResult.Failure -> {
                    view.showServerFailureToast()
                }
                is DataResult.NotSuccessfulError -> {
                    view.showNotSuccessfulErrorToast()
                }
            }
        }
    }
}
