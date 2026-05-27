package woowacourse.shopping.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import woowacourse.shopping.R
import woowacourse.shopping.ui.MainActivity

class PaymentReminderReceiver : BroadcastReceiver() {
    private companion object {
        private const val REQUEST_CODE_CONTENT = 2002
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != PaymentReminderScheduler.ACTION_SHOW_PAYMENT_REMINDER) return
        if (PaymentReminderPreference.isEnabled(context).not()) return
        if (canPostNotification(context).not()) return

        createNotificationChannel(context)

        val notification =
            NotificationCompat
                .Builder(context, PaymentReminderScheduler.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_payment_notification)
                .setContentTitle("결제 알림")
                .setContentText("아직 결제가 완료되지 않았어요")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(paymentPendingIntent(context))
                .build()

        NotificationManagerCompat
            .from(context)
            .notify(PaymentReminderScheduler.NOTIFICATION_ID, notification)
    }

    private fun paymentPendingIntent(context: Context): PendingIntent {
        val intent =
            Intent(context, MainActivity::class.java).apply {
                action = PaymentReminderScheduler.ACTION_OPEN_PAYMENT
                putExtra(PaymentReminderScheduler.EXTRA_OPEN_PAYMENT, true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        return PendingIntent.getActivity(
            context,
            REQUEST_CODE_CONTENT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createNotificationChannel(context: Context) {
        val channel =
            NotificationChannel(
                PaymentReminderScheduler.CHANNEL_ID,
                "결제 알림",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun canPostNotification(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
}
