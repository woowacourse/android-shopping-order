package woowacourse.shopping.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService

object NotificationChannels {
    const val PAYMENT_REMINDER_ID = "payment_reminder"

    fun register(context: Context) {
        val channel = NotificationChannel(
            PAYMENT_REMINDER_ID,
            "결제 알림",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "미결제 상품 알림"
        }
        context.getSystemService<NotificationManager>()
            ?.createNotificationChannel(channel)
    }
}
