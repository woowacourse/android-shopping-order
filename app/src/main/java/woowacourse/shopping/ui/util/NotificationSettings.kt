package woowacourse.shopping.ui.util

import android.content.Context

object NotificationSettings {
    private const val PREFS_NAME = "settings"
    private const val KEY_NOTIFICATION = "notification"
    private const val KEY_HAS_SCHEDULED_REMINDER = "has_scheduled_reminder"

    fun isNotificationEnabled(context: Context): Boolean {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sp.getBoolean(KEY_NOTIFICATION, true)
    }

    fun setNotificationEnabled(
        context: Context,
        enabled: Boolean,
    ) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sp.edit().putBoolean(KEY_NOTIFICATION, enabled).apply()
    }

    fun markReminderScheduled(
        context: Context,
        scheduled: Boolean,
    ) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sp.edit().putBoolean(KEY_HAS_SCHEDULED_REMINDER, scheduled).apply()
    }

    fun hasScheduledReminder(context: Context): Boolean {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sp.getBoolean(KEY_HAS_SCHEDULED_REMINDER, false)
    }
}
