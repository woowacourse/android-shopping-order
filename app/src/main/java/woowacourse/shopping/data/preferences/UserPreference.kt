package woowacourse.shopping.data.preferences

import android.content.Context
import android.content.SharedPreferences

object UserPreference : UserStore {
    private lateinit var prefs: SharedPreferences
    private const val USER_PREFERENCE_NAME = "user_prefs"
    private const val USER_TOKEN = "user_token"

    override var token: String
        get() = prefs.getString(USER_TOKEN, "") ?: ""
        set(value) {
            prefs.edit().putString(USER_TOKEN, value).apply()
        }

    fun init(context: Context) {
        prefs = context.getSharedPreferences(
            USER_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
    }
}
