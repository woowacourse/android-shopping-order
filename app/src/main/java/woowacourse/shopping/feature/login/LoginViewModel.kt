package woowacourse.shopping.feature.login

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.domain.model.Authorization
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData

class LoginViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _loginSuccessEvent = MutableSingleLiveData<String>()
    val loginSuccessEvent: SingleLiveData<String> get() = _loginSuccessEvent

    private val _loginErrorEvent = MutableSingleLiveData<LoginError>()
    val loginErrorEvent: SingleLiveData<LoginError> get() = _loginErrorEvent

    val id = ObservableField<String>("")
    val pw = ObservableField<String>("")

    fun login() {
        val idValue = id.get() ?: ""
        val pwValue = pw.get() ?: ""
        val basicKey = Authorization.getBasicKey(idValue, pwValue)

        viewModelScope.launch {
            try {
                cartRepository.checkValidBasicKey(basicKey)
                _loginSuccessEvent.postValue(basicKey)

            } catch (e: Exception) {
                _loginErrorEvent.postValue(LoginError.Network)
            }
        }
    }
}