package woowacourse.shopping.ui.paymentconfirm

import woowacourse.shopping.ui.model.UiUserPointInfo

interface PaymentConfirmContract {
    interface View {
        fun updateUserPointInfo(userPointInfo: UiUserPointInfo)
    }

    interface Presenter {
        val view: View

        fun fetchUserPointInfo()
    }
}
