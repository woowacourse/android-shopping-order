package woowacourse.shopping.data.local

import android.content.Context
import androidx.core.content.edit
import woowacourse.shopping.domain.repository.AuthRepository

class WoowaSharedPreference(context: Context) : AuthRepository {
    private val authSharedPreference = context.getSharedPreferences(AUTH, Context.MODE_PRIVATE)

    private var userToken: String?
        get() = authSharedPreference.getString(USER_TOKEN, null)
        set(value) = authSharedPreference.edit { putString(USER_TOKEN, value).apply() }

    companion object {
        const val AUTH = "AUTH"
        const val USER_TOKEN = "USER_TOKEN"
    }

    override fun setToken(token: String) {
        userToken = token
    }

    override fun getToken(): String? {
        return userToken
    }
}
