package woowacourse.shopping.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import woowacourse.shopping.MainActivity
import woowacourse.shopping.R
import woowacourse.shopping.notification.NotificationConstants.ORDER_CHANNEL_ID
import woowacourse.shopping.notification.NotificationConstants.ORDER_NOTIFICATION_ID

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        context ?: return
        val cartIds = intent?.getIntegerArrayListExtra("cartIds") ?: return

        val activityIntent =
            Intent(context, MainActivity::class.java).apply {
                putIntegerArrayListExtra("cartIds", cartIds)

                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notification =
            NotificationCompat
                .Builder(context, ORDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_cart)
                .setContentTitle("장바구니 결제 알림")
                .setContentText("아직 결제가 완료되지 않았어요")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(ORDER_NOTIFICATION_ID, notification)
    }
}
