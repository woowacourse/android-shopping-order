package woowacourse.shopping.data.preference

import android.content.Context
import androidx.core.content.edit

class UserPreference private constructor(context: Context) : User {
    private val preference = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

    override var token: String
        get() = preference.getString(KEY_USER, EMPTY_VALUE) ?: EMPTY_VALUE
        set(value) {
            preference.edit {
                putString(KEY_USER, value)
            }
        }

    companion object {
        private const val SHARED_PREFERENCE_NAME = "user_preference"
        private const val KEY_USER = "KEY_USER"
        private const val EMPTY_VALUE = ""

        @Volatile
        private var instance: UserPreference? = null

        fun getInstance(context: Context): UserPreference {
            synchronized(this) {
                instance?.let { return it }
                return UserPreference(context)
            }
        }
    }
}
