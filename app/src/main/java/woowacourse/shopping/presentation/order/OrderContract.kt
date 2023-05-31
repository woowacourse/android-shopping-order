package woowacourse.shopping.presentation.order

import woowacourse.shopping.presentation.model.OrderProductModel

interface OrderContract {
    interface Presenter {
        fun showUserTotalPoint()
        fun checkPointAble(usePointText: String)
        fun showOrderPrice()
        fun showPaymentPrice()
        fun addOrder()
        fun loadOrderItems()
    }

    interface View {
        fun setUserTotalPoint(point: Int?)
        fun setUsagePoint(pointText: String)
        fun setOrderPrice(price: Int)
        fun setPaymentPrice(price: Int)
        fun showAddOrderComplete(completeMessage: String?)
        fun setOrderItems(orderProductsModel: List<OrderProductModel>)
    }
}
