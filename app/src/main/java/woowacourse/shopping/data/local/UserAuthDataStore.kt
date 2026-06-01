package woowacourse.shopping.data.local

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import woowacourse.shopping.BuildConfig

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class UserAuthDataStore(
    private val context: Context,
) {
    companion object {
        private val USER_NAME = stringPreferencesKey("userName")
        private val USER_PASSWORD = stringPreferencesKey("userPassword")
        private val CONFIG_USER_NAME = BuildConfig.BASIC_AUTH_USER_NAME.takeIf { it.isNotBlank() }
        private val CONFIG_USER_PASSWORD = BuildConfig.BASIC_AUTH_PASSWORD.takeIf { it.isNotBlank() }
    }

    val userName: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_NAME]
        }

    val userPassword: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_PASSWORD]
        }

    suspend fun initializeDefaultUserAuth() {
        context.dataStore.edit { preferences ->
            if (preferences[USER_NAME].isNullOrBlank() && CONFIG_USER_NAME != null) {
                preferences[USER_NAME] = CONFIG_USER_NAME
            }
            if (preferences[USER_PASSWORD].isNullOrBlank() && CONFIG_USER_PASSWORD != null) {
                preferences[USER_PASSWORD] = CONFIG_USER_PASSWORD
            }
        }
    }

    val encodedUserAuthInfo: Flow<String?> =
        combine(userName, userPassword) { name, password ->
            if (name.isNullOrBlank() || password.isNullOrBlank()) {
                null
            } else {
                val credentials = "$name:$password"
                "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            }
        }
}
