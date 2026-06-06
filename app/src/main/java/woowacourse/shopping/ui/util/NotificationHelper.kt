package woowacourse.shopping.ui.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import woowacourse.shopping.R

object NotificationHelper {
    private const val CHANNEL_ID = "payment_reminder_channel"
    private const val CHANNEL_NAME = "결제 알림"
    private const val CHANNEL_DESCRIPTION = "결제를 완료하지 않은 경우 알림"
    private const val NOTIF_ID = 2000

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(CHANNEL_ID) != null) return

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply { description = CHANNEL_DESCRIPTION }
        nm.createNotificationChannel(channel)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showPaymentReminder(
        context: Context,
        contentIntent: PendingIntent,
    ) {
        val notification =
            NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("결제하실 시간이 지났습니다")
                .setContentText("아직 결제가 완료되지 않았습니다. 결제하러 가기")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(context).notify(NOTIF_ID, notification)
    }

    fun cancel(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIF_ID)
    }
}
