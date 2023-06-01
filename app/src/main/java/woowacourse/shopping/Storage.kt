package woowacourse.shopping

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

object Storage {
    private lateinit var sharedPreferences: SharedPreferences
    private val KEY_SERVER = "server"
    private val KEY_CREDENTIAL = "credential"
    var server: String
        get() = sharedPreferences.getString(KEY_SERVER, "") ?: ""
        set(value) {
            sharedPreferences
                .edit()
                .putString(KEY_SERVER, value)
                .apply()
        }
    var credential: String
        get() = sharedPreferences.getString(KEY_CREDENTIAL, "") ?: ""
        set(value) {
            sharedPreferences
                .edit()
                .putString(KEY_CREDENTIAL, "Basic $value")
                .apply()
        }

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("shopping", Application.MODE_PRIVATE)
        credential = "cmluZ2xvQGVtYWlsLmNvbTpyaW5nbG8xMDEwMjM1"
    }
}
