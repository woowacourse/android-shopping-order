package woowacourse.shopping.feature.orderHistory

import woowacourse.shopping.model.OrderHistoryProductUiModel
import woowacourse.shopping.model.OrderStateUiModel

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
//    private val orderHistoryRepository: OrderHistoryRepository
) : OrderHistoryContract.Presenter {

    private val product = OrderHistoryProductUiModel(
        0,
        1000,
        "2023-03-03",
        OrderStateUiModel.PENDING,
        "워터",
        "https://img.danawa.com/prod_img/500000/711/196/img/5196711_1.jpg?_v=20200724173034",
        4
    )

    override fun loadOrderHistory() {
//        val products = orderHistoryRepository.getOrderHistory()
        val products = List(10) { product }
        view.addOrderHistory(products)
    }

    override fun loadProducts() {
        val products = List(10) { product }
        view.addOrderHistory(products)
    }
}
