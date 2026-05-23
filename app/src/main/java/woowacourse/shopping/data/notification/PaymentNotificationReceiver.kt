package woowacourse.shopping.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import woowacourse.shopping.R

class PaymentNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        val notificationManager = context.getSystemService<NotificationManager>() ?: return

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "결제 미완료 알림",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
        notificationManager.createNotificationChannel(channel)

        val notification =
            NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("결제가 완료되지 않았어요.")
                .setContentText("지금 결제하면 오늘 받을 수 있어요!")
                .setAutoCancel(true)
                .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "payment_pending"
        const val NOTIFICATION_ID = 1001
    }
}
