package woowacourse.shopping.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock

object PaymentReminderScheduler {
    const val ACTION_SHOW_PAYMENT_REMINDER = "woowacourse.shopping.action.SHOW_PAYMENT_REMINDER"
    const val ACTION_OPEN_PAYMENT = "woowacourse.shopping.action.OPEN_PAYMENT"
    const val EXTRA_OPEN_PAYMENT = "open_payment"

    const val CHANNEL_ID = "payment_reminder"
    const val NOTIFICATION_ID = 1001

    private const val REQUEST_CODE_ALARM = 2001

    private const val REMINDER_DELAY_MILLIS = 5 * 60 * 1000L

    fun schedule(context: Context) {
        cancel(context)

        if (PaymentReminderPreference.isEnabled(context).not()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + REMINDER_DELAY_MILLIS,
            pendingIntent(context),
        )
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent(context))
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent =
            Intent(context, PaymentReminderReceiver::class.java).apply {
                action = ACTION_SHOW_PAYMENT_REMINDER
            }

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_ALARM,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
