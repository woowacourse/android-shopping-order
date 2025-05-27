package woowacourse.shopping.data.authentication.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import woowacourse.shopping.domain.authentication.UserAuthentication

class DefaultAuthenticationRepository(
    private val authDataSource: SharedPreferences,
) : AuthenticationRepository {
    override var id: String
        get() = authDataSource.getString(USER_ID_KEY, "").orEmpty()
        private set(value) = authDataSource.edit { putString(USER_ID_KEY, value) }

    override var password: String
        get() = authDataSource.getString(USER_PASSWORD_KEY, "").orEmpty()
        private set(value) = authDataSource.edit { putString(USER_PASSWORD_KEY, value) }

    override fun updateUserAuthentication(userAuthentication: UserAuthentication) {
        this.id = userAuthentication.id
        this.password = userAuthentication.password
    }

    companion object {
        private const val USER_ID_KEY = "id"
        private const val USER_PASSWORD_KEY = "password"

        @Suppress("ktlint:standard:property-naming")
        private var INSTANCE: AuthenticationRepository? = null

        fun initialize(authDataStore: SharedPreferences) {
            if (INSTANCE == null) {
                INSTANCE = DefaultAuthenticationRepository(authDataSource = authDataStore)
            }
        }

        fun get(): AuthenticationRepository = INSTANCE ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
