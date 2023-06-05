package woowacourse.shopping.view.payment

import woowacourse.shopping.model.uimodel.OrderProductUIModel

interface PaymentContract {
    interface View {
        var presenter: Presenter
        fun updateOrderProducts(orderItems: List<OrderProductUIModel>)
        fun showOrderDetail(orderId: Long)
        fun updatePoints(point: Int)
        fun updateTotalPrice(price: Int)
    }

    interface Presenter {
        fun updateOrderProducts(cartIds: Array<Long>)
        fun payOrderProducts(originalPrice: Int, points: Int)
        fun getPoints()
    }
}
