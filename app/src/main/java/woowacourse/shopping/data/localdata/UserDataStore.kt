package woowacourse.shopping.data.localdata

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore by preferencesDataStore(name = "user")

class UserDataStore(
    private val context: Context,
) {
    suspend fun saveUser(
        username: String,
        password: String,
    ) {
        context.userDataStore.edit { prefs ->
            prefs[USERNAME_KEY] = username
            prefs[PASSWORD_KEY] = password
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

    companion object {
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val PASSWORD_KEY = stringPreferencesKey("password")
    }
}
