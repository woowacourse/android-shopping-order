package woowacourse.shopping.presentation.order

import woowacourse.shopping.presentation.model.OrderCartModel

interface OrderContract {
    interface View {
        fun showOrderCarts(orderCarts: List<OrderCartModel>)
        fun showPoint(point: Int)
        fun updateTotalPrice(price: Int)
        fun showOrderSuccess()
        fun showOrderFail()
    }

    interface Presenter {
        fun initOrderCarts(orderCarts: List<OrderCartModel>)
        fun loadPoint()
        fun loadTotalPrice()
        fun checkPointOver(usingPoint: String)
        fun order(spendPoint: String)
    }
}
