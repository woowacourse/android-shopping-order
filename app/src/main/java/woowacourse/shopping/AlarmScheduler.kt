package woowacourse.shopping

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import woowacourse.shopping.data.source.local.notification.NotificationSettingDataSource

class AlarmScheduler(
    private val context: Context,
    private val requestCode: Int,
    private val receiver: Class<*>,
    private val notificationSettingDataSource: NotificationSettingDataSource,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(
        delayMillis: Long,
        intent: Intent,
    ) {
        if (!notificationSettingDataSource.isNotificationEnabled()) {
            cancel()
            return
        }
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + delayMillis,
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            ),
        )
    }

    fun cancel() {
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                requestCode,
                Intent(context, receiver),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE,
            ) ?: return
        alarmManager.cancel(pendingIntent)
    }
}
