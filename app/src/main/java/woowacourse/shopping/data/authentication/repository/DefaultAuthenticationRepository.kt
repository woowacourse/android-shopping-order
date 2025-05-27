package woowacourse.shopping.data.authentication.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import woowacourse.shopping.domain.authentication.UserAuthentication

class DefaultAuthenticationRepository(
    private val authDataStore: DataStore<Preferences>
) : AuthenticationRepository {
    override val id: String
        get() = authDataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }.toString()

    override val password: String
        get() = authDataStore.data.map { preferences ->
            preferences[USER_PASSWORD_KEY]
        }.toString()

    override suspend fun updateUserAuthentication(userAuthentication: UserAuthentication) {
        authDataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userAuthentication.id
            preferences[USER_PASSWORD_KEY] = userAuthentication.password
        }
    }

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("id")
        private val USER_PASSWORD_KEY = stringPreferencesKey("password")
    }
}
