package woowacourse.shopping.ui.orderDetail

import woowacourse.shopping.model.OrderUIModel

interface OrderDetailContract {
    interface View {
        fun setOrder(order: OrderUIModel)
        fun navigateToProductDetail(productId: Int)
    }

    interface Presenter {
        fun getOrderDetail()
        fun navigateToProductDetail(productId: Int)
    }
}
