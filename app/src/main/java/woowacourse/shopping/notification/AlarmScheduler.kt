package woowacourse.shopping.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import woowacourse.shopping.notification.NotificationConstants.ORDER_ALARM_REQUEST_CODE
import woowacourse.shopping.notification.NotificationConstants.ORDER_REMINDER_INTERVAL_MS

class AlarmScheduler(
    private val context: Context,
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(cartIds: List<Int>) {
        val intent =
            Intent(context, NotificationReceiver::class.java).apply {
                putIntegerArrayListExtra("cartIds", ArrayList(cartIds))
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                ORDER_ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val triggerTime = SystemClock.elapsedRealtime() + ORDER_REMINDER_INTERVAL_MS

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    pendingIntent,
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    pendingIntent,
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent,
            )
        }
    }

    fun cancel() {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                ORDER_ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        alarmManager.cancel(pendingIntent)
    }
}
