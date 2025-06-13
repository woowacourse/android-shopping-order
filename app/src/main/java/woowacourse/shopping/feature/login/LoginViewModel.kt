package woowacourse.shopping.feature.login

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.account.AccountRepository
import woowacourse.shopping.data.account.BasicKeyAuthorizationResult
import woowacourse.shopping.domain.model.Authorization
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData

class LoginViewModel(
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val _loginSuccessEvent = MutableSingleLiveData<Unit>()
    val loginSuccessEvent: SingleLiveData<Unit> get() = _loginSuccessEvent
    private val _loginErrorEvent = MutableSingleLiveData<LoginError>()
    val loginErrorEvent: SingleLiveData<LoginError> get() = _loginErrorEvent
    val id = ObservableField<String>("")
    val pw = ObservableField<String>("")

    fun login() {
        val idValue = id.get() ?: ""
        val pwValue = pw.get() ?: ""
        Authorization.setBasicKeyByIdPw(idValue, pwValue)
        viewModelScope.launch {
            val result =
                accountRepository.checkValidBasicKey(
                    Authorization.basicKey,
                )
            when (result) {
                BasicKeyAuthorizationResult.LoginSuccess -> {
                    Authorization.setLoginStatus(true)
                    saveBasicKey()
                }

                else -> {
                    _loginErrorEvent.setValue(LoginError.NotFound)
                    Authorization.setLoginStatus(false)
                }
            }
        }
    }

    private fun saveBasicKey() {
        viewModelScope.launch {
            val result = accountRepository.saveBasicKey()

            when {
                result.isSuccess -> _loginSuccessEvent.setValue(Unit)
                result.isFailure -> _loginErrorEvent.postValue(LoginError.NotFound)
            }
        }
    }
}
