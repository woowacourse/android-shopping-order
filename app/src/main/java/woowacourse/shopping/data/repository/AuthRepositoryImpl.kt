package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import woowacourse.shopping.data.localdata.UserDataStore

class AuthRepositoryImpl(
    private val userDataStore: UserDataStore,
) : AuthRepository {
    private val _userName = MutableStateFlow<String?>(null)
    private val _password = MutableStateFlow<String?>(null)

    override val userName = _userName.asStateFlow()
    override val password = _password.asStateFlow()

    override suspend fun loadCredentialsToMemory() {
        val credentials = userDataStore.userCredentialsFlow.first()

        _userName.value = credentials.username
        _password.value = credentials.password
    }
}
