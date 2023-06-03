package woowacourse.shopping.presentation.ui.myPage

interface MyPageContract {
    interface View {
        fun showCharge()
        fun requestRecharge()
    }

    interface Presenter {
        fun fetchCharge()
        fun recharge()
    }
}
