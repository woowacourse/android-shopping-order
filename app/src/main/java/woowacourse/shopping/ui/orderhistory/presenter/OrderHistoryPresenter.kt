package woowacourse.shopping.ui.orderhistory.presenter

import android.util.Log
import com.example.domain.model.Receipt
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.ui.orderhistory.uimodel.OrderHistory

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
    private val orderRepository: OrderRepository,
) : OrderHistoryContract.Presenter {

    override fun fetchOrderHistory() {
        orderRepository.getOrderHistory(
            onSuccess = { view.setOrderHistory(it.map { it.toUiState() }) },
            onFailure = { Log.d("ERROR_OrderHistoryPresenter", it.toString()) },
        )
    }

    private fun Receipt.toUiState() = OrderHistory(
        id = id,
        orderProducts = orderProducts,
        originPrice = originPrice,
        couponName = couponName,
        totalPrice = totalPrice,
    )
}
