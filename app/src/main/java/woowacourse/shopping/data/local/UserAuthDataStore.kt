package woowacourse.shopping.data.local

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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

    val userName: Flow<String> =
        dataStoreContext.dataStore.data
            .map { preferences ->
                preferences[USER_NAME] ?: "First_woosun"
            }

    val userPassword: Flow<String> =
        dataStoreContext.dataStore.data
            .map { preferences ->
                preferences[USER_PASSWORD] ?: "password"
            }

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

    val encodedUserAuthInfo: Flow<String> =
        combine(userName, userPassword) { name, password ->
            val credentials = "$name:$password"
            "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        }
}
