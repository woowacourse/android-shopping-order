package woowacourse.shopping.data.localdata

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

class ShoppingSharedPreferences(
    context: Context,
) {
    private val sharedPreference = context.getSharedPreferences(NAME, MODE_PRIVATE)

    fun saveIsNotification(isNotification: Boolean) {
        sharedPreference.edit(commit = true) {
            putBoolean(NOTIFICATION_KEY, isNotification)
        }
    }

    fun getIsNotification(): Boolean = sharedPreference.getBoolean(NOTIFICATION_KEY, false)

    companion object {
        private const val NAME = "setting"
        private const val NOTIFICATION_KEY = "notification"
    }
}
