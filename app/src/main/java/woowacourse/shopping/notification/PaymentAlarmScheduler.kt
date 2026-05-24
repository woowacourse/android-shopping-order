package woowacourse.shopping.notification

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.content.getSystemService
import woowacourse.shopping.BuildConfig

class PaymentAlarmScheduler(
    private val context: Context,
) {
    private val alarmManager = context.getSystemService<AlarmManager>()

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun schedule() {
        val triggerAt = System.currentTimeMillis() + BuildConfig.PAYMENT_ALARM_DELAY_MILLIS
        alarmManager?.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            createPendingIntent(),
        )
    }

    fun cancel() {
        alarmManager?.cancel(createPendingIntent())
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, PaymentReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private const val REQUEST_CODE = 1001
    }
}
