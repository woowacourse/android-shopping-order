package woowacourse.shopping.ui.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import woowacourse.shopping.ui.util.PaymentReminderContract.ACTION_PAYMENT_REMINDER
import woowacourse.shopping.ui.util.PaymentReminderContract.EXTRA_SELECTED_ITEM_IDS

object PaymentReminderScheduler {
    private const val REMINDER_REQUEST_CODE = 71025

    private fun makePendingIntent(
        context: Context,
        selectedItemIds: List<Int> = emptyList(),
    ): PendingIntent {
        val intent =
            Intent(context, PaymentReminderReceiver::class.java).apply {
                action = ACTION_PAYMENT_REMINDER
                putExtra(EXTRA_SELECTED_ITEM_IDS, selectedItemIds.toIntArray())
            }
        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        return PendingIntent.getBroadcast(context, REMINDER_REQUEST_CODE, intent, flags)
    }

    fun schedule(
        context: Context,
        delayMillis: Long = 5 * 60 * 1000L,
        selectedItemIds: List<Int> = emptyList(),
    ) {
        if (!NotificationSettings.isNotificationEnabled(context)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = makePendingIntent(context, selectedItemIds)
        val triggerAtMillis = System.currentTimeMillis() + delayMillis

        alarmManager.cancel(pendingIntent)
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent,
        )

        NotificationSettings.markReminderScheduled(context, true)
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = makePendingIntent(context)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        NotificationSettings.markReminderScheduled(context, false)
    }

    fun isScheduled(context: Context): Boolean = NotificationSettings.hasScheduledReminder(context)
}
