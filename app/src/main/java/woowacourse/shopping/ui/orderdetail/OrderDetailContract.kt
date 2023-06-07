package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.ui.model.OrderUiModel

interface OrderDetailContract {

    interface View {

        fun initView(order: OrderUiModel)

        fun showErrorMessage(errorMessage: String)
    }

    interface Presenter {

        fun getOrder()

        fun handleNavigator()
    }
}
