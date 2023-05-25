package woowacourse.shopping

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

object Storage {
    private lateinit var sharedPreferences: SharedPreferences
    private val KEY_SERVER = "server"
    var server: String
        get() = sharedPreferences.getString(KEY_SERVER, "") ?: ""
        set(value) {
            sharedPreferences
                .edit()
                .putString(KEY_SERVER, value)
                .apply()
        }

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("shopping", Application.MODE_PRIVATE)
    }
}
