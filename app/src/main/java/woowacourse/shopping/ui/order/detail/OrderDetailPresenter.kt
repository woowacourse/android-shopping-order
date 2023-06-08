package woowacourse.shopping.ui.order.detail

import android.util.Log
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
            onFailure = { errorMessage ->
                Log.d("error", "[ERROR] 데이터를 불러오는 데에 실패했습니다. : $errorMessage")
                view.showLoadFailed(errorMessage)
            },
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
