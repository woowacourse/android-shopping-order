package woowacourse.shopping.presentation.view.orderdetail

import woowacourse.shopping.data.model.OrderDetailEntity
import woowacourse.shopping.presentation.model.CartModel

interface OrderDetailContract {
    interface View {
        val presenter: Presenter

        fun setView(orderDetail: OrderDetailEntity, products: List<CartModel>)
        fun setTotalPriceView(totalPrice: Int)
        fun handleErrorView()
    }

    interface Presenter {
        fun initView()
    }
}
