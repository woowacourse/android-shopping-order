package woowacourse.shopping

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class Storage private constructor() {
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

    companion object {
        private lateinit var sharedPreferences: SharedPreferences
        private const val NAME = "shopping"
        private const val KEY_SERVER = "server"
        private const val KEY_CREDENTIAL = "credential"

        @Volatile
        private var instance: Storage? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: Storage().also {
                sharedPreferences = context.applicationContext.getSharedPreferences(NAME, Application.MODE_PRIVATE)
                it.credential = "cmluZ2xvQGVtYWlsLmNvbTpyaW5nbG8xMDEwMjM1"
                instance = it
            }
        }
    }
}
