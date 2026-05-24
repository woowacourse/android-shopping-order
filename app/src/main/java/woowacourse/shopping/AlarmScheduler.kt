package woowacourse.shopping

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class AlarmScheduler(
    private val context: Context,
    private val requestCode: Int,
    private val receiver: Class<*>,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val pendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, receiver),
            PendingIntent.FLAG_IMMUTABLE,
        )
    }

    fun schedule(delayMillis: Long) {
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + delayMillis,
            pendingIntent,
        )
    }

    fun cancel() {
        alarmManager.cancel(pendingIntent)
    }
}
