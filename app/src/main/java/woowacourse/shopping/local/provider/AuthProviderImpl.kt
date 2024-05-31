package woowacourse.shopping.local.provider

import android.content.SharedPreferences
import androidx.core.content.edit
import woowacourse.shopping.data.provider.AuthProvider

class AuthProviderImpl(private val sharedPreferences: SharedPreferences) : AuthProvider() {
    override var name: String
        get() = sharedPreferences.getString(KEY_AUTH_NAME, null) ?: ""
        set(value) {
            sharedPreferences.edit(true) {
                putString(KEY_AUTH_NAME, value)
            }
        }

    override var password: String
        get() = sharedPreferences.getString(KEY_AUTH_PASSWORD, null) ?: ""
        set(value) {
            sharedPreferences.edit(true) {
                putString(KEY_AUTH_PASSWORD, value)
            }
        }

    companion object {
        const val KEY_AUTH_NAME = "KEY_AUTH_NAME"
        const val KEY_AUTH_PASSWORD = "KEY_AUTH_PASSWORD"

        private var instance: AuthProviderImpl? = null

        fun setInstance(sharedPreferences: SharedPreferences) {
            instance =
                AuthProviderImpl(sharedPreferences = sharedPreferences).apply {
                    name = "junjange"
                    password = "password"
                }
        }

        fun getInstance(): AuthProviderImpl = requireNotNull(instance)
    }
}
