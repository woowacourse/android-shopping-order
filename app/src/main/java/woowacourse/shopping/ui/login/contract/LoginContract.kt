package woowacourse.shopping.ui.login.contract

interface LoginContract {
    interface View {

        fun getLoginState()
    }

    interface Presenter {

        fun postAuthInfo()
    }
}
