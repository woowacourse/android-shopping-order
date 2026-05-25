package woowacourse.shopping.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.NotificationRepository
import woowacourse.shopping.data.repository.preferences.SharedPreferenceNotificationRepository
import woowacourse.shopping.ui.shopping.ShoppingActivity

class PaymentAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val notificationRepository: NotificationRepository =
            SharedPreferenceNotificationRepository(context)

        if (!notificationRepository.isEnabled()) return

        val cartItemIds = intent.getStringExtra(PaymentNotificationConstants.EXTRA_CART_ITEM_IDS).orEmpty()
        if (cartItemIds.isBlank()) return

        val notificationManager =
            context.getSystemService(NotificationManager::class.java)

        createChannelIfNeeded(notificationManager)
        notificationManager.notify(
            PaymentNotificationConstants.NOTIFICATION_ID,
            NotificationCompat.Builder(context, PaymentNotificationConstants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_payment)
                .setContentTitle(PaymentNotificationConstants.NOTIFICATION_TITLE)
                .setContentText(PaymentNotificationConstants.NOTIFICATION_TITLE)
                .setAutoCancel(true)
                .setContentIntent(createContentIntent(context, cartItemIds))
                .build(),
        )
    }

    private fun createContentIntent(
        context: Context,
        cartItemIds: String,
    ): PendingIntent {
        val intent =
            Intent(context, ShoppingActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(PaymentNotificationConstants.EXTRA_CART_ITEM_IDS, cartItemIds)
            }

        return PendingIntent.getActivity(
            context,
            PaymentNotificationConstants.NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createChannelIfNeeded(notificationManager: NotificationManager) {
        if (notificationManager.getNotificationChannel(PaymentNotificationConstants.CHANNEL_ID) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    PaymentNotificationConstants.CHANNEL_ID,
                    PaymentNotificationConstants.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT,
                ),
            )
        }
    }
}
