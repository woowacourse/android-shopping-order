package woowacourse.shopping.data.alarm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import woowacourse.shopping.R
import woowacourse.shopping.ui.shopping.ShoppingActivity

class PayNotification(
    private val context: Context,
) {
    fun showPayReminderNotification() {
        if (!hasNotificationPermission()) return

        createNotificationChannel()

        val notification =
            NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pay_notification)
                .setContentTitle("결제 알림")
                .setContentText("아직 결제가 완료되지 않았어요")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(createContentIntent())
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat
            .from(context)
            .notify(NOTIFICATION_ID, notification)
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createContentIntent(): PendingIntent {
        val intent =
            Intent(context, ShoppingActivity::class.java).apply {
                action = R.string.ACTION_OPEN_PAY_SCREEN.toString()
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        return PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = CHANNEL_DESCRIPTION
            }

        val notificationManager =
            context.getSystemService(NotificationManager::class.java)

        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "pay_reminder"
        private const val CHANNEL_NAME = "결제 알림"
        private const val CHANNEL_DESCRIPTION = "결제 완료를 알려주는 알림"
        private const val NOTIFICATION_ID = 1001
    }
}
