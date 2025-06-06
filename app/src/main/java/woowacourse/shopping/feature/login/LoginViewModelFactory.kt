package woowacourse.shopping.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.carts.repository.CartRepository

class LoginViewModelFactory(
    private val cartRepository: CartRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(cartRepository) as T
        }
        throw IllegalArgumentException("알 수 없는 뷰 모델 클래스 입니다")
    }
}
