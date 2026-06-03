package woowacourse.shopping.data.local.userdata

import kotlinx.coroutines.flow.StateFlow

interface UserDataSource {
    val userName: StateFlow<String>
    val userPassword: StateFlow<String>
    val encodedUserAuthInfo: StateFlow<String>

    suspend fun saveUserAuth(name: String, pass: String)
    suspend fun clearUserAuth()

    fun isNotificationEnable(): Boolean
    fun setNotificationEnable(enabled: Boolean)
}