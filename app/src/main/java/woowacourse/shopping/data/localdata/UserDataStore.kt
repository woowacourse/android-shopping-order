package woowacourse.shopping.data.localdata

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDataStore(
    context: Context,
) {
    private val context = context.applicationContext
    private val Context.userDataStore by preferencesDataStore(name = "user")

    suspend fun saveUser(
        username: String,
        password: String,
    ) {
        context.userDataStore.edit { prefs ->
            prefs[USERNAME_KEY] = username
            prefs[PASSWORD_KEY] = password
        }
    }

    suspend fun saveIsNotification(isNotification: Boolean) {
        context.userDataStore.edit { prefs ->
            prefs[NOTIFICATION_KEY] = isNotification
        }
    }

    val username: Flow<String> =
        context.userDataStore.data.map { prefs ->
            prefs[USERNAME_KEY] ?: ""
        }

    val password: Flow<String> =
        context.userDataStore.data.map { prefs ->
            prefs[PASSWORD_KEY] ?: ""
        }

    val isNotification: Flow<Boolean> =
        context.userDataStore.data.map { prefs ->
            prefs[NOTIFICATION_KEY] ?: false
        }

    companion object {
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val PASSWORD_KEY = stringPreferencesKey("password")
        private val NOTIFICATION_KEY = booleanPreferencesKey("notification")
    }
}
