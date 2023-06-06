package woowacourse.shopping.ui.orderhistory.contract.presenter

import com.example.domain.repository.OrderHistoryRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.ui.orderhistory.contract.OrderHistoryContract

class OrderHistoryPresenter(
    val view: OrderHistoryContract.View,
    val repository: OrderHistoryRepository,
) : OrderHistoryContract.Presenter {
    override fun getOrderHistory() {
        repository.getOrderHistory() {
            view.setOrderHistory(it.map { order -> order.toUIModel() })
        }
    }
}
