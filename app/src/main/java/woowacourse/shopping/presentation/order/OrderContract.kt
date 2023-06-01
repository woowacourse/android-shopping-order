package woowacourse.shopping.presentation.order

import woowacourse.shopping.presentation.model.OrderCartInfoModel

interface OrderContract {
    interface View {
        fun showOrderCarts(orderCarts: List<OrderCartInfoModel>)
        fun showPoint(point: Int)
        fun updateTotalPrice(price: Int)
        fun showOrderSuccess()
        fun showOrderFail()
    }

    interface Presenter {
        fun initOrderCarts(orderCarts: List<OrderCartInfoModel>)
        fun loadPoint()
        fun loadTotalPrice()
        fun checkPointOver(usingPoint: String)
        fun order()
    }
}
