package woowacourse.shopping.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.util.Base64
import kotlinx.coroutines.flow.combine
import woowacourse.shopping.BuildConfig

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class UserAuthDataStore(private val context: Context) {
    companion object {
        private val USER_NAME = stringPreferencesKey("userName")
        private val USER_PASSWORD = stringPreferencesKey("userPassword")
        private val CONFIG_USER_NAME = BuildConfig.BASIC_AUTH_USER_NAME.takeIf { it.isNotBlank() }
        private val CONFIG_USER_PASSWORD = BuildConfig.BASIC_AUTH_PASSWORD.takeIf { it.isNotBlank() }
    }

    val userName: Flow<String?> = context.dataStore.data
            .map { preferences ->
                preferences[USER_NAME] ?: CONFIG_USER_NAME
            }

    val userPassword: Flow<String?> = context.dataStore.data
            .map { preferences ->
                preferences[USER_PASSWORD] ?: CONFIG_USER_PASSWORD
            }

    suspend fun saveUserAuth(
        userName: String,
        userPassword: String,
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = userName
            preferences[USER_PASSWORD] = userPassword
        }
    }

    suspend fun clearUserAuth() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_NAME)
            preferences.remove(USER_PASSWORD)
        }
    }

    val encodedUserAuthInfo: Flow<String?> = combine(userName, userPassword) { name, password ->
            if (name.isNullOrBlank() || password.isNullOrBlank()) {
                null
            } else {
                val credentials = "$name:$password"
                "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            }
        }
}
