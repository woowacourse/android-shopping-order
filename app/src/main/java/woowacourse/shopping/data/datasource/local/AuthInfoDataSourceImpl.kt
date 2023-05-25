package woowacourse.shopping.data.datasource.local

import android.content.Context
import android.content.SharedPreferences

class AuthInfoDataSourceImpl private constructor(context: Context) : AuthInfoDataSource {

    private val sharedPreference: SharedPreferences =
        context.getSharedPreferences(AUTH_INFO, Context.MODE_PRIVATE)

    override fun getAuthInfo(token: String) {
        sharedPreference.getString(USER_ACCESS_TOKEN, token)
    }

    override fun setAuthInfo(token: String) {
        sharedPreference.edit().putString(USER_ACCESS_TOKEN, token).apply()
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
