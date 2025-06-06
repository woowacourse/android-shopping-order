package woowacourse.shopping.feature.login

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.domain.model.Authorization
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData

class LoginViewModel(
    private val cartRepository: CartRepository,
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
                cartRepository.checkValidBasicKey(
                    Authorization.basicKey,
                )
            when (result) {
                is CartFetchResult.Error -> _loginErrorEvent.setValue(LoginError.NotFound)
                is CartFetchResult.Success -> {
                    when {
                        result.data == 200 -> {
                            Authorization.setLoginStatus(true)
                            saveBasicKey()
                        }
                        else -> _loginErrorEvent.postValue(LoginError.Network)
                    }
                }
            }
        }
    }

    private fun saveBasicKey() {
        viewModelScope.launch {
            val result = cartRepository.saveBasicKey()

            when {
                result.isSuccess -> _loginSuccessEvent.setValue(Unit)
                result.isFailure -> _loginErrorEvent.postValue(LoginError.NotFound)
            }
        }
    }
}
