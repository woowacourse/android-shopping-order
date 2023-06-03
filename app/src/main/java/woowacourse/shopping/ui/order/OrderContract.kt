package woowacourse.shopping.ui.order

import woowacourse.shopping.model.OrderInfoUIModel

interface OrderContract {
    interface View {
        fun initOrderPageInfo(orderInfo: OrderInfoUIModel)
    }

    interface Presenter {
        fun getOrderInfo(ids: List<Int>)
    }
}
