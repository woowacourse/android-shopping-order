package woowacourse.shopping.presentation.ui.myPage

interface MyPageContract {
    interface View {
        fun showCharge(amount: Int)
        fun requestRecharge()
        fun showError()
    }

    interface Presenter {
        fun fetchCharge()
        fun recharge(amount: Int)
    }
}
