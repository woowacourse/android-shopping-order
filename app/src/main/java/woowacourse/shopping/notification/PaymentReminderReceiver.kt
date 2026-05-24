package woowacourse.shopping.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import woowacourse.shopping.MainActivity
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.notification.permission.hasNotificationPermission

class PaymentReminderReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val app = context.applicationContext as ShoppingApplication

        val enabled =
            app.appContainer.settingsPreferences
                .isNotificationEnabled(default = false)
        if (!enabled) return

        if (!context.hasNotificationPermission()) return

        val notification = buildNotification(context)
        context
            .getSystemService<NotificationManager>()
            ?.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(context: Context): Notification {
        val contentIntent =
            PendingIntent.getActivity(
                context,
                REQUEST_CODE_CONTENT,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra(MainActivity.EXTRA_NAVIGATE_TO_PAYMENT, true)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        return NotificationCompat
            .Builder(context, NotificationChannels.PAYMENT_REMINDER_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("쇼핑")
            .setContentText("아직 결제가 완료되지 않았어요. 장바구니에서 확인해보세요")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val REQUEST_CODE_CONTENT = 2001
    }
}
