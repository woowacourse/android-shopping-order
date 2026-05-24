package woowacourse.shopping.ui.alarm

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.MainActivity

class PaymentAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val shoppingContext = context.applicationContext

        val notificationSetting = ShoppingApplication.notificationSetting
        if (!notificationSetting.isNotificationEnabled()) return

        val toPaymentIntent = Intent(shoppingContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "payment")
        }

        val tapIntent = PendingIntent.getActivity(
            shoppingContext,
            PENDING_REQUEST_CODE,
            toPaymentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(shoppingContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(PAYMENT_NOTIFICATION_TITLE)
            .setContentText(PAYMENT_NOTIFICATION_CONTENT)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(tapIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(shoppingContext)
        if (ActivityCompat.checkSelfPermission(
                shoppingContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }

    companion object {
        const val CHANNEL_ID = "payment_reminder_channel"
        private const val NOTIFICATION_ID = 1001
        private const val PENDING_REQUEST_CODE = 1
        private const val PAYMENT_NOTIFICATION_TITLE = "Shopping"
        private const val PAYMENT_NOTIFICATION_CONTENT = "결제가 아직 완료되지 않았어요!!"
    }
}
