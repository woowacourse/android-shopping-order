package woowacourse.shopping.feature.login

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
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
        cartRepository.checkValidBasicKey(
            Authorization.basicKey,
            { response ->
                when (response) {
                    200 -> {
                        Authorization.setLoginStatus(true)
                        saveBasicKey()
                    }
                    else -> _loginErrorEvent.postValue(LoginError.NotFound)
                }
            },
            {
                _loginErrorEvent.postValue(LoginError.Network)
            },
        )
    }

    private fun saveBasicKey() {
        cartRepository.saveBasicKey({
            _loginSuccessEvent.postValue(Unit)
        }, {
            _loginErrorEvent.postValue(LoginError.NotFound)
        })
    }
}
