package woowacourse.shopping.ui.order

import woowacourse.shopping.uimodel.OrderInfoUIModel

interface OrderContract {
    interface View {
        fun initOrderPageInfo(orderInfo: OrderInfoUIModel)
        fun setButtonEnable(isEnabled: Boolean)
        fun showPointErrorMessage(errorCode: Int)
        fun updatePurchasePrice(discountPrice: Int, totalPrice: Int)
    }

    interface Presenter {
        fun getOrderInfo(ids: List<Int>)
        fun checkPointAvailable(pointPrice: Int?)
        fun setTotalPrice(discountPrice: Int?)
    }
}
