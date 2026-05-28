package woowacourse.shopping.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class PayReminderAlarm(
    private val context: Context,
) {
    private val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule() {
        cancel()

        val triggerAtMillis = System.currentTimeMillis() + FIVE_MINUTES
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            createPendingIntent(),
        )
    }

    fun cancel() {
        alarmManager.cancel(createPendingIntent())
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, PayReminderReceiver::class.java)

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_PAY_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private const val FIVE_MINUTES = 5 * 60 * 1000L
        private const val REQUEST_CODE_PAY_REMINDER = 1001
    }
}
