package woowacourse.shopping.data.local.userdata

import kotlinx.coroutines.flow.StateFlow

class UserDataSourceImpl(
    private val authDataStore: UserAuthDataStore,
    private val notificationSettingStorage: NotificationSettingStorage,
) : UserDataSource {
    override val userName: StateFlow<String> = authDataStore.userName
    override val userPassword: StateFlow<String> = authDataStore.userPassword
    override val encodedUserAuthInfo: StateFlow<String> = authDataStore.encodedUserAuthInfo

    override suspend fun saveUserAuth(
        name: String,
        pass: String,
    ) {
        authDataStore.saveUserAuth(name, pass)
    }

    override suspend fun clearUserAuth() {
        authDataStore.clearUserAuth()
    }

    override fun isNotificationEnable(): Boolean = notificationSettingStorage.isNotificationEnabled()

    override fun setNotificationEnable(enabled: Boolean) {
        notificationSettingStorage.setNotificationEnabled(enabled)
    }
}
