package woowacourse.shopping.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class ShoppingOrderSharedPreference(context: Context) {
    private val serverSharedPreference: SharedPreferences =
        context.getSharedPreferences(SERVER, Context.MODE_PRIVATE)

    var baseUrl: String
        get() = serverSharedPreference.getString(SERVER, "") ?: ""
        set(value) = serverSharedPreference.edit { putString(SERVER, value).apply() }

    private fun clear() {
        serverSharedPreference.edit { clear().apply() }
    }

    companion object {
        private const val SERVER = "SERVER"
    }
}
