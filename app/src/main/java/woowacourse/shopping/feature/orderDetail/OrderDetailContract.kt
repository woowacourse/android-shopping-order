package woowacourse.shopping.feature.orderDetail

import woowacourse.shopping.model.OrderDetailProductUiModel
import woowacourse.shopping.model.OrderInfoUiModel

interface OrderDetailContract {

    interface View {
        fun initAdapter(orderProducts: List<OrderDetailProductUiModel>)
        fun setUpView(orderInfo: OrderInfoUiModel)
        fun showErrorMessage(t: Throwable)
    }

    interface Presenter {
        fun loadProducts()
        fun cancelOrder(id: Int)
    }
}
