package woowacourse.shopping.presentation.view.orderdetail

import woowacourse.shopping.presentation.model.CartProductModel

interface OrderDetailContract {
    interface View {
        fun showOrderDateView(oderDate: String)
        fun showOrderProductItemView(orderProducts: List<CartProductModel>)
        fun showOrderPriceView(orderPrice: Int)
        fun showTotalPriceView(totalPrice: Int)
        fun showUsedPointView(usedPoint: Int)
        fun showSavedPointView(savedPoint: Int)
        fun handleErrorView(messageId: Int)
    }

    interface Presenter {
        fun loadOrderDetail(orderId: Long)
    }
}
