package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import woowacourse.shopping.data.localdata.UserDataStore

class AuthRepository(
    private val userDataStore: UserDataStore,
) {
    private val _userName = MutableStateFlow<String?>(null)
    private val _password = MutableStateFlow<String?>(null)

    val userName = _userName.asStateFlow()
    val password = _password.asStateFlow()

    suspend fun loadCredentialsToMemory() {
        _userName.value = userDataStore.username.first()
        _password.value = userDataStore.password.first()
    }
}
