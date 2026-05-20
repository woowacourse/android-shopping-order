package woowacourse.shopping.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthDataStore(
    private val dataStore: DataStore<Preferences>,
) {
    private val usernameKey = stringPreferencesKey("username")
    private val passwordKey = stringPreferencesKey("password")

    suspend fun saveAuthInfo(
        username: String,
        password: String,
    ) {
        dataStore.edit { preferences ->
            preferences[usernameKey] = username
            preferences[passwordKey] = password
        }
    }

    val username: Flow<String?> = dataStore.data.map { it[usernameKey] }

    val password: Flow<String?> = dataStore.data.map { it[passwordKey] }
}
