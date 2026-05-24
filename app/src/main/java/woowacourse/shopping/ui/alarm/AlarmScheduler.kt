package woowacourse.shopping.ui.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build


class AlarmScheduler(context: Context) {
    private val applicationContext = context.applicationContext
    private val alarmManager =
        applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun createAlarmSchedule(delayMillis: Long) {
        val intent = Intent(applicationContext, PaymentAlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            PAYMENT_ALARM_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + delayMillis

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
            return
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } catch (e: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancel() {
        val intent = Intent(applicationContext, PaymentAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            PAYMENT_ALARM_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        private const val PAYMENT_ALARM_ID = 1
    }
}