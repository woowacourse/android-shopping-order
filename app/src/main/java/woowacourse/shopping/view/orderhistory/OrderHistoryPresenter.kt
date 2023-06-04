package woowacourse.shopping.view.orderhistory

import woowacourse.shopping.model.uimodel.OrderUIModel
import woowacourse.shopping.server.retrofit.MembersService
import woowacourse.shopping.server.retrofit.createResponseCallback

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
    service: MembersService
) : OrderHistoryContract.Presenter {

    init {
        service.getOrders().enqueue(
            createResponseCallback(
                onSuccess = { orders ->
                    val orderHistory = orders.map {
                        OrderUIModel(it.orderId, it.orderPrice, it.totalAmount, it.previewName)
                    }
                    view.updateOrders(orderHistory)
                },
                onFailure = { throw IllegalStateException("주문 목록을 불러오는데 실패했습니다.") }
            )
        )
    }
}
