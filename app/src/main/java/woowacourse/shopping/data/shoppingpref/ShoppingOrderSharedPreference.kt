package woowacourse.shopping.data.shoppingpref

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class ShoppingOrderSharedPreference(context: Context) {
    private val serverSharedPreference: SharedPreferences =
        context.getSharedPreferences(SERVER, Context.MODE_PRIVATE)

    var baseUrl: String
        get() = serverSharedPreference.getString(SERVER, "") ?: ""
        set(value) = serverSharedPreference.edit { putString(SERVER, value).apply() }

    var userInfo: String
        get() = serverSharedPreference.getString(USERINFO, "") ?: ""
        set(value) = serverSharedPreference.edit { putString(USERINFO, value).apply() }

    private fun clear() {
        serverSharedPreference.edit { clear().apply() }
    }

    companion object {
        private const val USERINFO = "USERINFO"
        private const val SERVER = "SERVER"
        private var shoppingOrderSharedPreference: ShoppingOrderSharedPreference? = null
        fun getInstance(context: Context): ShoppingOrderSharedPreference {
            return shoppingOrderSharedPreference ?: ShoppingOrderSharedPreference(context).also {
                shoppingOrderSharedPreference = it
            }
        }
    }
}
