package woowacourse.shopping.data.datasource.local

import android.content.Context
import android.content.SharedPreferences

class AuthInfoDataSourceImpl private constructor(context: Context) : AuthInfoDataSource {

    private val sharedPreference: SharedPreferences =
        context.getSharedPreferences(AUTH_INFO, Context.MODE_PRIVATE)

    init {
        setAuthInfo()
    }

    override fun getAuthInfo(): String? {
        return sharedPreference.getString(USER_ACCESS_TOKEN, null)
    }

    override fun setAuthInfo() {
        sharedPreference.edit().putString(USER_ACCESS_TOKEN, "basic bG9waToxMjM0").apply()
    }

    companion object {
        private const val AUTH_INFO = "AUTH_INFO"
        private const val USER_ACCESS_TOKEN = "USER_ACCESS_TOKEN"

        private val authInfoDataSourceImpl: AuthInfoDataSourceImpl? = null
        fun getInstance(context: Context): AuthInfoDataSourceImpl {
            return authInfoDataSourceImpl ?: synchronized(this) {
                AuthInfoDataSourceImpl(context)
            }
        }
    }
}
