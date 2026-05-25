package woowacourse.shopping.ui.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import woowacourse.shopping.R

object NotificationHelper {
    private const val CHANNEL_ID = "payment_reminder_channel"
    private const val CHANNEL_NAME = "결제 알림"
    private const val NOTIF_ID = 2000

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "결제를 완료하지 않은 경우 알림"
                }
                nm.createNotificationChannel(channel)
            }
        }
    }

    private fun makeContentIntent(context: Context): PendingIntent {

        val intent = Intent(context, woowacourse.shopping.ui.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(woowacourse.shopping.ui.MainActivity.EXTRA_NAVIGATE_TO_PAYMENT, true)
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(context, 0, intent, flags)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showPaymentReminder(context: Context) {
        val contentIntent = makeContentIntent(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("결제하실 시간이 지났습니다")
            .setContentText("아직 결제가 완료되지 않았습니다. 결제하러 가기")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)

        val nm = NotificationManagerCompat.from(context)
        nm.notify(NOTIF_ID, builder.build())

        NotificationSettings.markReminderScheduled(context, false)
    }

    fun cancel(context: Context) {
        val nm = NotificationManagerCompat.from(context)
        nm.cancel(NOTIF_ID)
    }
}




