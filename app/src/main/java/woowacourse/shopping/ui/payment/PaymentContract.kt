package woowacourse.shopping.ui.payment

import woowacourse.shopping.ui.model.BasketProduct
import woowacourse.shopping.ui.model.User

interface PaymentContract {

    interface View {

        fun initView(
            user: User,
            basketProducts: List<BasketProduct>,
            totalPrice: Int,
        )

        fun showOrderDetail(orderId: Int)
    }

    interface Presenter {

        fun getUser()

        fun addOrder(usingPoint: Int)
    }
}
