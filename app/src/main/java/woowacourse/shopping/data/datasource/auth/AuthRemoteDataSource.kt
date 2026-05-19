package woowacourse.shopping.data.datasource.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthRemoteDataSource(
    private val dataStore: DataStore<Preferences>,
) : AuthDataSource {
    override suspend fun load(): String {
        return dataStore.data.map { preference ->
            preference[TOKEN_KEY]
        }.first()
            ?: ""
    }

    override suspend fun save(token: String) {
        dataStore.edit { preference ->
            preference[TOKEN_KEY] = token
        }
    }

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
    }
}
