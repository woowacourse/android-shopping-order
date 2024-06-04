package woowacourse.shopping.data.local.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class ShoppingPreferencesManager(context: Context) {
    private val shoppingPreferences = getShoppingPreferencesManager(context)

    fun getString(
        key: String,
        defaultValue: String? = DEFAULT_VALUE,
    ): String? {
        return shoppingPreferences?.getString(key, defaultValue)
    }

    fun setString(
        key: String,
        newValue: String,
    ): String {
        shoppingPreferences?.edit()?.putString(key, newValue)?.apply()
        return newValue
    }

    companion object {
        const val KEY_USERNAME = "username"
        const val KEY_PASSWORD = "password"
        private val DEFAULT_VALUE = null
        private const val SHOPPING_PREFERENCES = "shopping_prefs"

        private fun getShoppingPreferencesManager(context: Context): SharedPreferences? {
            return context.applicationContext.getSharedPreferences(
                SHOPPING_PREFERENCES,
                MODE_PRIVATE,
            )
        }
    }
}
