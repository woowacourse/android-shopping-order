package woowacourse.shopping.ui.order

import woowacourse.shopping.uimodel.OrderInfoUIModel

interface OrderContract {
    interface View {
        fun initOrderPageInfo(orderInfo: OrderInfoUIModel)
        fun setButtonEnable(isEnabled: Boolean)
        fun updatePurchasePrice(discountPrice: Int, totalPrice: Int)
        fun showPointErrorMessage(errorCode: Int)
        fun showOrderSuccessMessage()
        fun navigateToShopping()
    }

    interface Presenter {
        fun getOrderInfo(ids: List<Int>)
        fun checkPointAvailable(pointPrice: Int?)
        fun setTotalPrice(discountPrice: Int?)
        fun order(point: Int)
    }
}
