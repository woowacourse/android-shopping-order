package woowacourse.shopping.data.datasource.local

import android.content.Context
import android.content.SharedPreferences

class TokenSharedPreference private constructor(context: Context) {

    private val sharedPreference: SharedPreferences =
        context.getSharedPreferences(USER_TOKEN, Context.MODE_PRIVATE)

    fun setToken(token: String) {
        sharedPreference.edit().putString(USER_TOKEN, token).apply()
    }

    fun getToken(defValue: String): String? = sharedPreference.getString(USER_TOKEN, defValue)

    companion object {
        private const val USER_TOKEN = "USER_TOKEN"

        private val tokenSharedPreference: TokenSharedPreference? = null

        fun getInstance(context: Context): TokenSharedPreference {
            return tokenSharedPreference ?: synchronized(this) {
                TokenSharedPreference(context)
            }
        }
    }
}
