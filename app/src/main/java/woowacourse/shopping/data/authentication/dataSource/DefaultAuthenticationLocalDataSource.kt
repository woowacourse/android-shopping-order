package woowacourse.shopping.data.authentication.dataSource

import android.content.SharedPreferences
import androidx.core.content.edit

class DefaultAuthenticationLocalDataSource(
    private val authDataSource: SharedPreferences,
) : AuthenticationLocalDataSource {
    override var id: String
        get() = authDataSource.getString(USER_ID_KEY, "").orEmpty()
        private set(value) = authDataSource.edit { putString(USER_ID_KEY, value) }

    override var password: String
        get() = authDataSource.getString(USER_PASSWORD_KEY, "").orEmpty()
        private set(value) = authDataSource.edit { putString(USER_PASSWORD_KEY, value) }

    override fun updateId(id: String) {
        this.id = id
    }

    override fun updatePassword(password: String) {
        this.password = password
    }

    companion object {
        private const val USER_ID_KEY = "id"
        private const val USER_PASSWORD_KEY = "password"
    }
}
