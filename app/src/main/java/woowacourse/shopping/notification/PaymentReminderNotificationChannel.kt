package woowacourse.shopping.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import woowacourse.shopping.R

fun createPaymentReminderNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val notificationManager = context.getSystemService(NotificationManager::class.java)
    val channel =
        NotificationChannel(
            PAYMENT_REMINDER_CHANNEL_ID,
            PAYMENT_REMINDER_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.payment_reminder_channel_description)
        }

    notificationManager.createNotificationChannel(channel)
}
