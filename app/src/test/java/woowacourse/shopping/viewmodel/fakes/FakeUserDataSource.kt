package woowacourse.shopping.viewmodel.fakes

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.data.local.userdata.UserDataSource

class FakeUserDataSource : UserDataSource {
    override val userName: StateFlow<String> = MutableStateFlow("user")
    override val userPassword: StateFlow<String> = MutableStateFlow("password")
    override val encodedUserAuthInfo: StateFlow<String> = MutableStateFlow("Basic dXNlcjpwYXNzd29yZA==")

    private var isNotificationEnabled = true

    override suspend fun saveUserAuth(name: String, pass: String) {
        // Not needed for current tests
    }

    override suspend fun clearUserAuth() {
        // Not needed for current tests
    }

    override fun isNotificationEnable(): Boolean = isNotificationEnabled

    override fun setNotificationEnable(enabled: Boolean) {
        isNotificationEnabled = enabled
    }
}
