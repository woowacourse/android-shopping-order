package woowacourse.shopping.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import woowacourse.shopping.IntentKeys
import woowacourse.shopping.R

class PaymentNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val selectedCartItemIds =
            intent
                .getLongArrayExtra(IntentKeys.SELECTED_CART_ID_KEY)
                ?.toList()
                .orEmpty()
        if (selectedCartItemIds.isEmpty()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        createNotificationChannel(context)
        val contentIntent =
            PaymentNotificationIntentFactory.createContentPendingIntent(
                context = context,
                selectedCartItemIds = selectedCartItemIds,
            )
        val notification =
            NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_payment_notification)
                .setContentTitle("미결제 알림")
                .setContentText("아직 결제가 완료되지 않았어요")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build()

        try {
            NotificationManagerCompat
                .from(context)
                .notify(
                    PaymentNotificationIntentFactory.createNotificationId(selectedCartItemIds),
                    notification,
                )
        } catch (_: SecurityException) {
            return
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            )
        context
            .getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "payment_notification"
        private const val CHANNEL_NAME = "미결제 알림"
    }
}
