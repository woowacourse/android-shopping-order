package woowacourse.shopping.ui.ordercomplete.presenter

import android.util.Log
import com.example.domain.repository.OrderRepository

class OrderCompletePresenter(
    private val view: OrderCompleteContract.View,
    private val orderRepository: OrderRepository,
) : OrderCompleteContract.Presenter {
    override fun getReceipt() {
        orderRepository.getReceipt(
            onSuccess = { view.setReceipt(it) },
            onFailure = { Log.d("ERROR_OrderCompletePresenter", it.toString()) },
        )
    }
}
