package woowacourse.shopping.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import woowacourse.shopping.MainActivity
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
        val orderItemsJson = intent?.getStringExtra(EXTRA_ORDER_ITEMS_JSON) ?: ""
        val orderAmount = intent?.getLongExtra(EXTRA_ORDER_AMOUNT, 0) ?: 0
        val mainIntent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("NAVIGATION_TARGET", "PAYMENT_SCREEN")
                putExtra("ORDER_ITEMS_JSON", orderItemsJson)
                putExtra("ORDER_AMOUNT", orderAmount)
            }

        val pendingIntentFlags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

        val contentPendingIntent =
            PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                mainIntent,
                pendingIntentFlags,
            )

        val notification =
            NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("결제가 완료되지 않았어요.")
                .setContentText("지금 결제하면 오늘 받을 수 있어요!")
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "payment_pending"
        private const val NOTIFICATION_ID = 1001

        private const val EXTRA_ORDER_ITEMS_JSON = "extra_order_items_json"
        private const val EXTRA_ORDER_AMOUNT = "extra_order_amount"
    }
}
