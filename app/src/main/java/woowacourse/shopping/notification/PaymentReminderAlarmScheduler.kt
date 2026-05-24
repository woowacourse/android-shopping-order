package woowacourse.shopping.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class PaymentReminderAlarmScheduler(
    private val context: Context,
) {
    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(AlarmManager::class.java)
    }

    fun schedule(selectedProductIds: Set<Long>) {
        val pendingIntent = createAlarmPendingIntent(selectedProductIds)
        val triggerAtMillis = System.currentTimeMillis() + PAYMENT_REMINDER_DELAY_MILLIS
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent,
        )
    }

    fun cancel() {
        val pendingIntent = createAlarmPendingIntent(emptySet())
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun createAlarmPendingIntent(selectedProductIds: Set<Long>): PendingIntent {
        val alarmIntent =
            Intent(context, PaymentReminderReceiver::class.java).apply {
                action = ACTION_SHOW_PAYMENT_REMINDER
                putExtra(EXTRA_SELECTED_PRODUCT_IDS, selectedProductIds.toLongArray())
            }

        return PendingIntent.getBroadcast(
            context,
            PAYMENT_REMINDER_ALARM_REQUEST_CODE,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
