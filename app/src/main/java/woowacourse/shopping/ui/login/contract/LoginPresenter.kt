package woowacourse.shopping.ui.login.contract

import com.example.domain.repository.LoginRepository

class LoginPresenter(
    serverKey: Int,
    private val view: LoginContract.View,
    private val loginRepository: LoginRepository,
) : LoginContract.Presenter {

//    init {
//        NetworkModule.getServerKey(serverKey)
//    }

    override fun postAuthInfo() {
        loginRepository.postAuthInfo()
    }
}
