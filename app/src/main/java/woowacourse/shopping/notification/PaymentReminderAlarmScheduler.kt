package woowacourse.shopping.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object PaymentReminderAlarmScheduler {
    private const val REMINDER_DELAY_MILLIS = 5 * 60 * 1000L
    private const val REQUEST_CODE = 1001
    internal const val ACTION_PAYMENT_REMINDER =
        "woowacourse.shopping.action.PAYMENT_REMINDER"

    // 5분 뒤의 알림을 예약
    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val pendingIntent =
            createPendingIntent(
                context = context,
                flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
                ?: return

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + REMINDER_DELAY_MILLIS,
            pendingIntent,
        )
    }

    // 기존 알림 취소
    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val pendingIntent =
            createPendingIntent(
                context = context,
                flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            )
                ?: return

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun createPendingIntent(
        context: Context,
        flags: Int,
    ): PendingIntent? {
        val intent =
            Intent(context, PaymentReminderReceiver::class.java).apply {
                action = ACTION_PAYMENT_REMINDER
            }

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            flags,
        )
    }
}
