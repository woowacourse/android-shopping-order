package woowacourse.shopping.repository

import android.util.Base64
import kotlinx.coroutines.flow.first
import woowacourse.shopping.storage.datastore.AuthDataStore

// 저장된 username/password 읽어와서 Basic base64(username:password)로 변환
class AuthHeaderProvider(
    private val authDataStore: AuthDataStore,
) {
    suspend fun getAuthorizationHeader(): String? {
        val username = authDataStore.username.first()
        val password = authDataStore.password.first()

        if (username.isNullOrBlank() || password.isNullOrBlank()) {
            return null
        }
        val raw = "$username:$password"
        val encoded = Base64.encodeToString(raw.toByteArray(), Base64.NO_WRAP)
        return "Basic $encoded"
    }
}
