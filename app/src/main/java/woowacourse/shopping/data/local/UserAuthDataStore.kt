package woowacourse.shopping.data.local

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import woowacourse.shopping.ShoppingApplication

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class UserAuthDataStore(
    context: ShoppingApplication,
) {
    private val dataStoreContext = context.applicationContext

    companion object {
        private val USER_NAME = stringPreferencesKey("userName")
        private val USER_PASSWORD = stringPreferencesKey("userPassword")
    }

    val userName: StateFlow<String> =
        dataStoreContext.dataStore.data
            .map { preferences ->
                preferences[USER_NAME] ?: "First_woosun"
            }.stateIn(
                scope = CoroutineScope(Dispatchers.IO),
                started = SharingStarted.Eagerly,
                initialValue = "First_woosun",
            )

    val userPassword: StateFlow<String> =
        dataStoreContext.dataStore.data
            .map { preferences ->
                preferences[USER_PASSWORD] ?: "password"
            }.stateIn(
                scope = CoroutineScope(Dispatchers.IO),
                started = SharingStarted.Eagerly,
                initialValue = "password",
            )

    suspend fun saveUserAuth(
        userName: String,
        userPassword: String,
    ) {
        dataStoreContext.dataStore.edit { preferences ->
            preferences[USER_NAME] = userName
            preferences[USER_PASSWORD] = userPassword
        }
    }

    suspend fun clearUserAuth() {
        dataStoreContext.dataStore.edit { preferences ->
            preferences.remove(USER_NAME)
            preferences.remove(USER_PASSWORD)
        }
    }

    val encodedUserAuthInfo: StateFlow<String> =
        combine(userName, userPassword) { name, password ->
            val credentials = "$name:$password"
            "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = "Basic " + Base64.encodeToString("${userName.value}:${userPassword.value}".toByteArray(), Base64.NO_WRAP),
        )
}
