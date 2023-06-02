package woowacourse.shopping.presentation.view.orderdetail

import woowacourse.shopping.presentation.model.CartModel

interface OrderDetailContract {
    interface View {
        fun setOrderDateView(oderDate: String)
        fun setOrderProductItemView(orderProducts: List<CartModel>)
        fun setOrderPriceView(orderPrice: Int)
        fun setTotalPriceView(totalPrice: Int)
        fun setUsedPointView(usedPoint: Int)
        fun setSavedPointView(savedPoint: Int)
        fun handleErrorView()
    }

    interface Presenter {
        fun loadOrderDetail(orderId: Long)
    }
}
