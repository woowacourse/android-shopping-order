package woowacourse.shopping.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.notification.permission.hasNotificationPermission
import woowacourse.shopping.ui.navigation.PaymentRoute

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
        val ids = intent.getLongArrayExtra(EXTRA_IDS)?.toList() ?: emptyList()

        if (!context.hasNotificationPermission()) return

        val notification = buildNotification(context, ids)
        context
            .getSystemService<NotificationManager>()
            ?.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(context: Context, ids: List<Long>): Notification {
        val query = ids.joinToString("&") { "ids=$it" }
        val deepLinkUri = "${PaymentRoute.DEEP_LINK}?$query".toUri()
        val contentIntent =
            TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(
                    Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
                        setPackage(context.packageName)
                    }
                )
                getPendingIntent(
                    REQUEST_CODE_CONTENT,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

        return NotificationCompat
            .Builder(context, NotificationChannels.PAYMENT_REMINDER_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("결제")
            .setContentText("아직 결제가 완료되지 않았어요.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()
    }

    companion object {
        const val EXTRA_IDS = "extra_ids"
        private const val NOTIFICATION_ID = 1001
        private const val REQUEST_CODE_CONTENT = 2001
    }
}
