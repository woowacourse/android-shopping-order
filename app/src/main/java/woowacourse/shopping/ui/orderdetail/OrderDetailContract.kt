package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.ui.model.OrderUiModel

interface OrderDetailContract {

    interface View {

        val navigator: OrderDetailNavigator

        fun initView(order: OrderUiModel)

        fun showErrorMessage(errorMessage: String)
    }

    interface Presenter {

        fun getOrder()

        fun handleNavigator()
    }
}
