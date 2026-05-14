package woowacourse.shopping.data.source.local.auth

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import woowacourse.shopping.dataStore

class DefaultAuthDataSource(
    private val context: Context,
) : AuthDataSource {
    private val authTokenKey = stringPreferencesKey("auth_token")

    private fun authTokenFlow(): Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[authTokenKey] ?: ""
        }

    override suspend fun getToken(): String = authTokenFlow().first()

    override suspend fun saveToken(
        id: String,
        password: String,
    ) {
        context.dataStore.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences[authTokenKey] = createToken(id, password)
            }
        }
    }

    private fun createToken(
        id: String,
        password: String,
    ) = Base64.encodeToString("$id:$password".toByteArray(), Base64.NO_WRAP)
}
