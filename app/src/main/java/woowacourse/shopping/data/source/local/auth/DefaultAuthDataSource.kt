package woowacourse.shopping.data.source.local.auth

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import woowacourse.shopping.dataStore
import java.io.ByteArrayOutputStream

class DefaultAuthDataSource(
    private val context: Context,
    private val cryptoManager: CryptoManager,
) : AuthDataSource {
    private val authTokenKey = stringPreferencesKey("auth_token")

    private fun authTokenFlow(): Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[authTokenKey] ?: ""
        }

    override suspend fun getToken(): String {
        val savedToken = authTokenFlow().first()

        if (savedToken.isBlank()) return ""

        val encryptedBytes = Base64.decode(savedToken, Base64.NO_WRAP)

        return cryptoManager
            .decrypt(encryptedBytes.inputStream())
            .decodeToString()
    }

    override suspend fun saveToken(
        id: String,
        password: String,
    ) {
        val token = createToken(id, password)

        context.dataStore.updateData {
            it.toMutablePreferences().also { preferences ->
                val outputStream = ByteArrayOutputStream()

                cryptoManager.encrypt(
                    bytes = token.toByteArray(),
                    outputStream = outputStream,
                )

                preferences[authTokenKey] =
                    Base64.encodeToString(
                        outputStream.toByteArray(),
                        Base64.NO_WRAP,
                    )
            }
        }
    }

    private fun createToken(
        id: String,
        password: String,
    ) = Base64.encodeToString("$id:$password".toByteArray(), Base64.NO_WRAP)
}
