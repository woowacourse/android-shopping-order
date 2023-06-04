package woowacourse.shopping.presentation.view.orderdetail

import woowacourse.shopping.presentation.model.CartModel

interface OrderDetailContract {
    interface View {
        val presenter: Presenter

        fun setOrderDateView(date: String)
        fun setOrderProductsView(products: List<CartModel>)
        fun handleErrorView()
    }

    interface Presenter {
        fun initView()
    }
}
