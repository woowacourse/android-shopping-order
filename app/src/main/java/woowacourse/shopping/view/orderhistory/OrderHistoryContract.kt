package woowacourse.shopping.view.orderhistory

import woowacourse.shopping.model.uimodel.OrderUIModel

interface OrderHistoryContract {

    interface View {
        var presenter: Presenter
        fun updateOrders(orders: List<OrderUIModel>)
    }

    interface Presenter
}
