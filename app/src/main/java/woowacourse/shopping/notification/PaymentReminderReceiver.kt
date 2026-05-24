package woowacourse.shopping.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingActivity

class PaymentReminderReceiver : BroadcastReceiver() {
    @RequiresPermission(POST_NOTIFICATIONS_PERMISSION)
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != ACTION_SHOW_PAYMENT_REMINDER) return

        if (!hasNotificationPermission(context)) return

        createPaymentReminderNotificationChannel(context)

        val selectedProductIds = intent.getLongArrayExtra(EXTRA_SELECTED_PRODUCT_IDS) ?: longArrayOf()
        val openPaymentIntent =
            Intent(context, ShoppingActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra(EXTRA_OPEN_PAYMENT_FROM_REMINDER, true)
                putExtra(EXTRA_SELECTED_PRODUCT_IDS, selectedProductIds)
            }

        val contentPendingIntent =
            PendingIntent.getActivity(
                context,
                PAYMENT_REMINDER_OPEN_REQUEST_CODE,
                openPaymentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notification =
            NotificationCompat
                .Builder(context, PAYMENT_REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.shopping_cart_icon)
                .setContentTitle(context.getString(R.string.payment_reminder_title))
                .setContentText(context.getString(R.string.payment_reminder_message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(contentPendingIntent)
                .build()

        NotificationManagerCompat.from(context).notify(PAYMENT_REMINDER_NOTIFICATION_ID, notification)
    }

    private fun hasNotificationPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            POST_NOTIFICATIONS_PERMISSION,
        ) == PackageManager.PERMISSION_GRANTED
}
