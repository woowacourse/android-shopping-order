package woowacourse.shopping.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.account.AccountRepository

class LoginViewModelFactory(
    private val accountRepository: AccountRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(accountRepository) as T
        }
        throw IllegalArgumentException("알 수 없는 뷰 모델 클래스 입니다")
    }
}
